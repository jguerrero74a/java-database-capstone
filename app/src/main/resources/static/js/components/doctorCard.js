/* doctorCard.js */

// Importaciones de servicios y utilidades
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../patientDashboard.js"; // O el archivo donde resida la lógica del overlay

/**
 * Crea y devuelve un elemento DOM para una tarjeta de doctor individual.
 * @param {Object} doctor - Objeto con la información del médico.
 */
export function createDoctorCard(doctor) {
    // 1. Crea el Contenedor Principal de la Tarjeta
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // 2. Obtén el rol del usuario desde localStorage
    const role = localStorage.getItem("userRole");

    // 3. Crear sección de información del doctor
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialty}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    // Se asume que doctor.availability es un arreglo de horarios
    availability.textContent = `Availability: ${doctor.availability.join(", ")}`;

    // Añadir elementos individuales al contenedor de info
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // 4. Crear Contenedor de Botón (Acciones)
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // 5. Agregar botones condicionalmente según el rol
    
    // === ROL: ADMINISTRADOR ===
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("delete-btn");

        removeBtn.addEventListener("click", async () => {
            const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
            if (confirmDelete) {
                const token = localStorage.getItem("token");
                try {
                    const result = await deleteDoctor(doctor.id, token);
                    if (result) {
                        alert("Doctor deleted successfully");
                        card.remove(); // Elimina la tarjeta del DOM
                    }
                } catch (error) {
                    console.error("Error deleting doctor:", error);
                    alert("Failed to delete doctor.");
                }
            }
        });
        actionsDiv.appendChild(removeBtn);
    } 
    
    // === ROL: PACIENTE (NO CONECTADO) ===
    else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");

        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });
        actionsDiv.appendChild(bookNow);
    } 
    
    // === ROL: PACIENTE CONECTADO ===
    else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");

        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Session expired. Please login again.");
                window.location.href = "/";
                return;
            }
            try {
                const patientData = await getPatientData(token);
                // Muestra el overlay de reserva pasando el evento, doctor y datos del paciente
                showBookingOverlay(e, doctor, patientData);
            } catch (error) {
                console.error("Error fetching patient data:", error);
            }
        });
        actionsDiv.appendChild(bookNow);
    }

    // 6. Ensamblaje Final
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    // 7. Devolver la tarjeta final
    return card;
}
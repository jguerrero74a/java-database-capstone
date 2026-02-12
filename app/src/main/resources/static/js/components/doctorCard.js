/* doctorCard.js */

import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
// Importamos la función desde el dashboard
import { showBookingOverlay } from "../patientDashboard.js"; 

/**
 * Crea y devuelve un elemento DOM para una tarjeta de doctor individual.
 */
export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialty}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    // Verificación de seguridad por si no hay horarios
    const times = doctor.availability ? doctor.availability.join(", ") : "Not specified";
    availability.textContent = `Availability: ${times}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // === ROL: ADMINISTRADOR ===
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("delete-btn");

        removeBtn.addEventListener("click", async () => {
            if (confirm(`Are you sure you want to delete Dr. ${doctor.name}?`)) {
                const token = localStorage.getItem("token");
                try {
                    const result = await deleteDoctor(doctor.id, token);
                    if (result.success) {
                        alert("Doctor deleted successfully");
                        card.remove();
                    }
                } catch (error) {
                    alert("Failed to delete doctor.");
                }
            }
        });
        actionsDiv.appendChild(removeBtn);
    } 
    
    // === ROL: PACIENTE (NO CONECTADO) ===
    else if (role === "patient" || !role) {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");

        bookNow.addEventListener("click", () => {
            alert("Please login as a patient to book an appointment.");
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
                // Aquí llamamos a la función importada
                showBookingOverlay(e, doctor, patientData);
            } catch (error) {
                console.error("Error fetching patient data:", error);
                alert("Error loading booking options.");
            }
        });
        actionsDiv.appendChild(bookNow);
    }

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
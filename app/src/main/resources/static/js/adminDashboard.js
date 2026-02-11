/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/

/* adminDashboard.js - Gestión de Médicos para el Administrador */

// 1. Importar Módulos Requeridos
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

/**
 * Vinculación de Eventos y Carga Inicial
 */
document.addEventListener('DOMContentLoaded', () => {
    // Vincular botón "Agregar Médico"
    const addDocBtn = document.getElementById('addDocBtn');
    if (addDocBtn) {
        addDocBtn.addEventListener('click', () => {
            openModal('addDoctor');
        });
    }

    // Configurar oyentes para búsqueda y filtros
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

    // Cargar tarjetas al inicio
    loadDoctorCards();
});

/**
 * Función: loadDoctorCards
 * Obtiene todos los médicos y los muestra en el tablero.
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error cargando médicos:", error);
    }
}

/**
 * Función: filterDoctorsOnChange
 * Recoge los valores actuales de filtro/búsqueda y actualiza la UI.
 */
async function filterDoctorsOnChange() {
    try {
        const name = document.getElementById("searchBar").value || "null";
        const time = document.getElementById("filterTime").value || "null";
        const specialty = document.getElementById("filterSpecialty").value || "null";

        const doctors = await filterDoctors(name, time, specialty);
        
        const contentDiv = document.getElementById("content");
        if (doctors.length === 0) {
            contentDiv.innerHTML = "<p>No se encontraron médicos con los filtros seleccionados.</p>";
        } else {
            renderDoctorCards(doctors);
        }
    } catch (error) {
        console.error("Error al filtrar:", error);
        alert("Ocurrió un error al filtrar los médicos.");
    }
}

/**
 * Función: renderDoctorCards
 * Función utilitaria para limpiar el contenedor y renderizar una lista de médicos.
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = ""; // Limpia el contenido existente

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

/**
 * Función: adminAddDoctor
 * Recoge los datos del formulario modal y guarda un nuevo médico.
 * Se asigna a window para ser accesible desde el modal.
 */
window.adminAddDoctor = async function () {
    // 1. Verificar token
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Error: No se encontró un token de sesión válido.");
        return;
    }

    // 2. Recoger valores del formulario
    const name = document.getElementById("docName").value;
    const specialty = document.getElementById("docSpecialty").value;
    const email = document.getElementById("docEmail").value;
    const password = document.getElementById("docPassword").value;
    const mobile = document.getElementById("docMobile").value;
    
    // Recoger disponibilidad de los checkboxes
    const availability = Array.from(document.querySelectorAll('input[name="availability"]:checked'))
                              .map(cb => cb.value);

    // 3. Crear objeto doctor
    const doctor = {
        name,
        specialty,
        email,
        password,
        mobile,
        availability
    };

    // 4. Enviar solicitud
    try {
        const result = await saveDoctor(doctor, token);
        
        if (result.success) {
            alert("¡Médico agregado exitosamente!");
            // 5. Refrescar UI
            location.reload(); 
        } else {
            alert("Fallo al guardar: " + result.message);
        }
    } catch (error) {
        console.error("Error en adminAddDoctor:", error);
        alert("Error inesperado al intentar agregar al médico.");
    }
};
/* patientDashboard.js – Ver y Filtrar Médicos */

// Importar Módulos Requeridos
import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';

/**
 * Inicialización al cargar el DOM
 */
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));

    const searchBar = document.getElementById("searchBar");
    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    
    const filterTime = document.getElementById("filterTime");
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    
    const filterSpec = document.getElementById("filterSpecialty");
    if (filterSpec) filterSpec.addEventListener("change", filterDoctorsOnChange);
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to load doctors:", error);
    }
}

async function filterDoctorsOnChange() {
    const searchBarValue = document.getElementById("searchBar").value.trim();
    const filterTimeValue = document.getElementById("filterTime").value;
    const filterSpecialtyValue = document.getElementById("filterSpecialty").value;

    const name = searchBarValue.length > 0 ? searchBarValue : "none";
    const time = filterTimeValue.length > 0 ? filterTimeValue : "none";
    const specialty = filterSpecialtyValue.length > 0 ? filterSpecialtyValue : "none";

    try {
        const doctors = await filterDoctors(name, time, specialty);
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to filter doctors:", error);
    }
}

export function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;
    contentDiv.innerHTML = "";

    if (doctors && doctors.length > 0) {
        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
}

/**
 * Función: showBookingOverlay (NUEVA Y EXPORTADA)
 * Maneja la visualización de la reserva.
 */
export function showBookingOverlay(event, doctor, patientData) {
    console.log("Mostrando overlay para:", doctor.name, "Paciente:", patientData.name);
    // Aquí va tu lógica para mostrar el formulario de reserva (Overlay)
    // Por ahora, un ejemplo sencillo para confirmar que funciona:
    alert(`Reserva para Dr. ${doctor.name}\nPaciente: ${patientData.name}\nEspecialidad: ${doctor.specialty}`);
}

window.signupPatient = async function () {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const result = await patientSignup({ name, email, password, phone, address });
    if (result.success) {
        alert("✅ Success!");
        window.location.reload();
    } else {
        alert("❌ " + result.message);
    }
};

window.loginPatient = async function () {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await patientLogin({ email, password });
    if (response.ok) {
        const result = await response.json();
        localStorage.setItem('token', result.token);
        localStorage.setItem('userRole', 'loggedPatient'); 
        alert("✅ Login successful!");
        window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
        alert('❌ Invalid credentials!');
    }
};
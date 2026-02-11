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
    // Cargar Tarjetas de Médicos al inicio
    loadDoctorCards();

    // Vincular Disparadores de Modal
    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));

    // Lógica de Búsqueda y Filtro (Listeners)
    document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
    document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
    document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);
});

/**
 * Función: loadDoctorCards
 * Obtiene y muestra todos los médicos disponibles.
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to load doctors:", error);
    }
}

/**
 * Función: filterDoctorsOnChange
 * Activa el filtrado dinámico según la entrada del usuario.
 */
async function filterDoctorsOnChange() {
    const searchBarValue = document.getElementById("searchBar").value.trim();
    const filterTimeValue = document.getElementById("filterTime").value;
    const filterSpecialtyValue = document.getElementById("filterSpecialty").value;

    const name = searchBarValue.length > 0 ? searchBarValue : null;
    const time = filterTimeValue.length > 0 ? filterTimeValue : null;
    const specialty = filterSpecialtyValue.length > 0 ? filterSpecialtyValue : null;

    try {
        const doctors = await filterDoctors(name, time, specialty);
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to filter doctors:", error);
        alert("❌ An error occurred while filtering doctors.");
    }
}

/**
 * Utilidad de Renderizado: renderDoctorCards
 * Limpia el contenedor e inyecta las tarjetas de los médicos.
 */
export function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
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
 * Manejo de Registro de Pacientes
 */
window.signupPatient = async function () {
    try {
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const phone = document.getElementById("phone").value;
        const address = document.getElementById("address").value;

        const data = { name, email, password, phone, address };
        const result = await patientSignup(data);

        if (result.success) {
            alert("✅ " + result.message);
            // Cerrar modal (si tienes acceso al elemento) y recargar
            const modal = document.getElementById("modal");
            if (modal) modal.style.display = "none";
            window.location.reload();
        } else {
            alert("❌ " + result.message);
        }
    } catch (error) {
        console.error("Signup failed:", error);
        alert("❌ An error occurred while signing up.");
    }
};

/**
 * Manejar el Inicio de Sesión del Paciente
 */
window.loginPatient = async function () {
    try {
        const email = document.getElementById("loginEmail").value; // Asegúrate que el ID sea correcto en tu HTML
        const password = document.getElementById("loginPassword").value;

        const data = { email, password };
        const response = await patientLogin(data);

        if (response.ok) {
            const result = await response.json();
            // Almacenar token y rol
            localStorage.setItem('token', result.token);
            if (typeof selectRole === 'function') selectRole('loggedPatient');
            
            alert("✅ Login successful!");
            window.location.href = '/pages/loggedPatientDashboard.html';
        } else {
            alert('❌ Invalid credentials!');
        }
    } catch (error) {
        console.error("Error during login:", error);
        alert("❌ Failed to Login. Please try again.");
    }
};
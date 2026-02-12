/* index.js - Manejo de Inicio de Sesión Basado en Roles */

// Importar Módulos Requeridos
import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// Definir constantes para los endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

/**
 * Configurar los escuchadores de eventos al cargar la página
 */
window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    const doctorBtn = document.getElementById('doctorLogin');

    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};

/**
 * Implementar el manejador de inicio de sesión de Admin
 */
window.adminLoginHandler = async function () {
    const usernameField = document.getElementById('adminUsername');
    const passwordField = document.getElementById('adminPassword');

    if (!usernameField || !passwordField) {
        console.error("No se encontraron los campos de entrada de Admin");
        return;
    }

    const username = usernameField.value;
    const password = passwordField.value;
    const admin = { username, password };

    try {
        console.log("Intentando login de Admin...");
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            
            // Verificamos que el backend realmente envió un token
            if (data.token) {
                console.log("Login exitoso. Guardando token...");
                localStorage.setItem('token', data.token);
                
                // Llamar a selectRole DESPUÉS de asegurar que el token existe
                if (typeof selectRole === 'function') {
                    selectRole('admin');
                } else {
                    console.error("La función selectRole no está disponible globalmente.");
                }
            } else {
                console.error("El servidor no devolvió un token en la respuesta.");
                alert("Error en la respuesta del servidor.");
            }
        } else {
            alert("¡Credenciales inválidas!");
        }
    } catch (error) {
        console.error("Error en el inicio de sesión del Admin:", error);
        alert("Ocurrió un error inesperado. Por favor, intente de nuevo.");
    }
};

/**
 * Implementar el manejador de inicio de sesión de Doctor
 */
window.doctorLoginHandler = async function () {
    const usernameField = document.getElementById('doctorUsername');
    const passwordField = document.getElementById('doctorPassword');

    if (!usernameField || !passwordField) {
        console.error("No se encontraron los campos de entrada de Doctor");
        return;
    }

    const username = usernameField.value;
    const password = passwordField.value;
    const doctor = { username, password };

    try {
        console.log("Intentando login de Doctor...");
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            
            if (data.token) {
                console.log("Login exitoso. Guardando token...");
                localStorage.setItem('token', data.token);
                
                if (typeof selectRole === 'function') {
                    selectRole('doctor');
                }
            } else {
                console.error("El servidor no devolvió un token.");
                alert("Error en la sesión del Doctor.");
            }
        } else {
            alert("¡Credenciales inválidas!");
        }
    } catch (error) {
        console.error("Error en el inicio de sesión del Doctor:", error);
        alert("Ocurrió un error inesperado.");
    }
};
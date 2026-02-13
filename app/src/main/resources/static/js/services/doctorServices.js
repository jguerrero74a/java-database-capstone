import { API_BASE_URL } from '../config/config.js';

const DOCTOR_API = `${API_BASE_URL}/doctor`;

/**
 * Function: getDoctors
 * Purpose: Fetch the list of all doctors from the API
 */
export async function getDoctors() {
    try {
        const response = await fetch(`${DOCTOR_API}/all`);
        const data = await response.json();
        return data.doctors || [];
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

/**
 * Function: deleteDoctor
 * Purpose: Delete a specific doctor using their ID and an authentication token
 */
export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/delete/${id}/${token}`, {
            method: 'DELETE'
        });
        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Doctor eliminado correctamente"
        };
    } catch (error) {
        console.error("Error deleting doctor:", error);
        return { success: false, message: "Error al intentar eliminar el doctor" };
    }
}

/**
 * Function: saveDoctor
 * Purpose: Save (create) a new doctor using a POST request
 */
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/add/${token}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });
        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Doctor guardado con éxito"
        };
    } catch (error) {
        console.error("Error saving doctor:", error);
        return { success: false, message: "Error de red al guardar el doctor" };
    }
}

/**
 * Function: filterDoctors
 * Purpose: Fetch doctors based on filtering criteria (name, specialty, and time)
 */
export async function filterDoctors(name, specialty, time) {
    try {
const n = (name && name !== "none") ? name : "all";
        const s = (specialty && specialty !== "none") ? specialty : "all";
        const t = (time && time !== "none") ? time : "all";

console.log(`Petición al servidor: /doctor/filter/${n}/${s}/${t}`);

        const response = await fetch(`${DOCTOR_API}/filter/${n}/${s}/${t}`);
        
        if (response.ok) {
            const data = await response.json();
            return data.doctors || [];
        } else {
            return [];
        }
    } catch (error) {
        console.error("Error en la petición de filtrado:", error);
        return { doctors: [] };
    }
}
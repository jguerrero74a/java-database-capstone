// render.js

function selectRole(role) {
    setRole(role);
    const token = localStorage.getItem('token');
    console.log("Rol seleccionado:", role);
    console.log("Token recuperado:", token);
    if (role === "admin") {
      if (token && token !== "null" && token !== "undefined") {
          window.location.assign(`adminDashboard/${token}`);
      } else {
          console.error("Error: Token no encontrado para Admin");
      }    
    } 
    else if (role === "patient") {
      window.location.href = "/pages/patientDashboard.html";
    } 
    else if (role === "doctor") {
      if (token && token !== "null" && token !== "undefined") {
          window.location.assign(`doctorDashboard/${token}`);
      } else {
          console.error("Error: Token no encontrado para Doctor");
      }
    }
    else if(role === "loggedPatient") {
      window.location.href = "loggedPatientDashboard.html";
    }
  }
  
  
  function renderContent() {
    const role = getRole();
    if (!role) {
      window.location.href = "/"; // if no role, send to role selection page
      return;
    }
  }
  
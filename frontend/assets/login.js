document.getElementById("login-form").addEventListener("submit", async event => {
  event.preventDefault();
  const message = document.getElementById("message");
  message.textContent = "";
  try {
    const session = await UamsApi.request("/auth/login", {
      method: "POST",
      body: JSON.stringify({
        username: document.getElementById("username").value.trim(),
        password: document.getElementById("password").value
      })
    });
    const selectedRole = document.getElementById("role").value;
    if (session.role !== selectedRole) throw new Error(`This is not a ${UamsApi.label(selectedRole)} account.`);
    UamsApi.setSession(session);
    location.href = { ADMIN: "/admin.html", FACULTY: "/faculty.html", STUDENT: "/student.html" }[session.role];
  } catch (error) {
    UamsApi.clearSession();
    message.textContent = error.message;
  }
});

const UAMS_API_BASE = window.UAMS_API_BASE || "http://localhost:8082/api";

const UamsApi = {
  key: "uamsSession",

  getSession() {
    try { return JSON.parse(sessionStorage.getItem(this.key) || "null"); }
    catch { return null; }
  },

  setSession(session) {
    sessionStorage.setItem(this.key, JSON.stringify(session));
  },

  clearSession() {
    sessionStorage.removeItem(this.key);
  },

  requireRole(role) {
    const session = this.getSession();
    if (!session || session.role !== role) {
      this.clearSession();
      location.href = "/login.html";
      return null;
    }
    return session;
  },

  async request(path, options = {}) {
    const session = this.getSession();
    const headers = new Headers(options.headers || {});
    if (options.body) headers.set("Content-Type", "application/json");
    if (session?.token) headers.set("Authorization", `Bearer ${session.token}`);
    const response = await fetch(`${UAMS_API_BASE}${path}`, { ...options, headers });
    const type = response.headers.get("content-type") || "";
    const data = response.status === 204 ? null : type.includes("json") ? await response.json() : await response.text();
    if (!response.ok) throw new Error(data?.message || `Request failed (${response.status})`);
    return data;
  },

  async download(path, filename) {
    const session = this.getSession();
    const headers = new Headers();
    if (session?.token) headers.set("Authorization", `Bearer ${session.token}`);
    const response = await fetch(`${UAMS_API_BASE}${path}`, { headers });
    if (!response.ok) {
      const data = await response.json().catch(() => null);
      throw new Error(data?.message || `Download failed (${response.status})`);
    }
    const url = URL.createObjectURL(await response.blob());
    const link = document.createElement("a"); link.href = url; link.download = filename;
    document.body.append(link); link.click(); link.remove(); URL.revokeObjectURL(url);
  },

  async logout() {
    try { await this.request("/auth/logout", { method: "POST" }); } catch {}
    this.clearSession();
    location.href = "/login.html";
  },

  label(value) {
    return String(value || "").toLowerCase().replaceAll("_", " ").replace(/\b\w/g, c => c.toUpperCase());
  },

  initValidation() {
    const messageFor = field => {
      const validity = field.validity;
      if (validity.valueMissing) return "This field is required.";
      if (validity.typeMismatch) return "Enter a valid value.";
      if (validity.tooShort) return `Use at least ${field.minLength} characters.`;
      if (validity.tooLong) return `Use no more than ${field.maxLength} characters.`;
      if (validity.rangeUnderflow) return `Value must be at least ${field.min}.`;
      if (validity.rangeOverflow) return `Value must be no more than ${field.max}.`;
      if (validity.patternMismatch) return field.dataset.patternMessage || "Enter a valid format.";
      return "Check this value.";
    };

    document.querySelectorAll("form").forEach(form => {
      form.noValidate = true;
      const fields = [...form.querySelectorAll("input, select, textarea")];
      fields.forEach(field => {
        if (field.required) {
          const label = form.querySelector(`label[for="${field.id}"]`) || field.previousElementSibling;
          if (label?.tagName === "LABEL" && !label.querySelector(".required")) {
            const mark = document.createElement("span"); mark.className = "required"; mark.textContent = " *"; label.append(mark);
          }
        }
        const error = document.createElement("small");
        error.className = "field-error"; error.id = `${field.id || field.name}-error`;
        field.insertAdjacentElement("afterend", error); field.setAttribute("aria-describedby", error.id);
        const validate = () => {
          const invalid = !field.checkValidity();
          field.classList.toggle("is-invalid", invalid);
          error.textContent = invalid ? messageFor(field) : "";
          return !invalid;
        };
        field.addEventListener("blur", validate);
        field.addEventListener("input", () => { if (field.classList.contains("is-invalid")) validate(); });
        field.addEventListener("change", () => { if (field.classList.contains("is-invalid")) validate(); });
        field._validate = validate;
      });
      form.addEventListener("submit", event => {
        const valid = fields.map(field => field._validate()).every(Boolean);
        if (!valid) { event.preventDefault(); event.stopImmediatePropagation(); form.querySelector(".is-invalid")?.focus(); }
      });
    });
  }
};

window.UamsApi = UamsApi;
UamsApi.initValidation();

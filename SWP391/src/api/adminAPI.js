import axiosClient from "./axiosClient";

const adminAPI = {
  login: (data) => axiosClient.post("/admin/auth/login", data),
  createAdmin: (data) => axiosClient.post("/admin/auth/createAdmin", data),
  getAllAdmins: () => axiosClient.get("/admin/auth/getAllAdmins"),
  getAdminById: (id) => axiosClient.get(`/admin/auth/ById/${id}`),
  updateAdmin: (id, data) => axiosClient.put(`/admin/auth/updateAdmin/${id}`, data),
  deleteAdmin: (id) => axiosClient.delete(`/admin/auth/deleteAdmin/${id}`),
};

export default adminAPI;
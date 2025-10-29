import axiosClient from "./axiosClient";

const adminDriverAPI = {
  getAllDrivers: () => axiosClient.get("/api/admin/drivers"),
  getDriverById: (id) => axiosClient.get(`/api/admin/drivers/${id}`),
  createDriver: (data) => axiosClient.post("/api/admin/drivers", data),
  updateDriver: (id, data) => axiosClient.put(`/api/admin/drivers/${id}`, data),
  deleteDriver: (id) => axiosClient.delete(`/api/admin/drivers/${id}`),
};

export default adminDriverAPI;
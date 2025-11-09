import axiosClient from "./axiosClient";

const adminDriverAPI = {
  // getAllDrivers: () => axiosClient.get("/api/admin/drivers"),
  // getDriverById: (id) => axiosClient.get(`/api/admin/drivers/${id}`),
  // createDriver: (data) => axiosClient.post(`/api/admin/drivers`, data),
  // updateDriver: (id, data) => axiosClient.put(`/api/admin/drivers/${id}`, data),
  // deleteDriver: (id) => axiosClient.delete(`/api/admin/drivers/${id}` ),
  // Lấy danh sách tất cả tài xế
  getAllDrivers: () => axiosClient.get("/api/admin/drivers"),

  // Lấy tài xế theo ID
  getDriverById: (id) => axiosClient.get(`/api/admin/drivers/${id}`),

  // Tạo mới tài xế
  createDriver: (data) => axiosClient.post("/api/admin/drivers", data),

  // Cập nhật thông tin tài xế
  updateDriver: (id, data) => axiosClient.put(`/api/admin/drivers/${id}`, data),

  // Xoá tài xế
  deleteDriver: (id) => axiosClient.delete(`/api/admin/drivers/${id}`),
};

export default adminDriverAPI;
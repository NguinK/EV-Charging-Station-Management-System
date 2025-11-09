import axiosClient from "./axiosClient";

const chargingPointAPI = {
  getPointById: (pointId) => axiosClient.get(`/api/charging-points/${pointId}`),
  createPoint: (data) => axiosClient.post("/api/charging-points/charging-points", data),
  updatePointStatus: (pointId, data) => axiosClient.put(`/api/charging-points/${pointId}/status`, data),
  updatePointPricing: (pointId, data) => axiosClient.put(`/api/charging-points/${pointId}/pricing`, data),
  deletePoint: (pointId) => axiosClient.delete(`/api/charging-points/${pointId}`),
  getPointsByStation: (stationId) => axiosClient.get(`/api/charging-points/station/${stationId}`),
  getAvailablePoints: (stationId) => axiosClient.get(`/api/charging-points/station/${stationId}/available`),
  searchPoints: () => axiosClient.get("/api/charging-points/search"),
};

export default chargingPointAPI;

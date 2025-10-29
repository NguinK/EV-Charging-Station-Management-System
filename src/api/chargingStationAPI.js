import axiosClient from "./axiosClient";

const chargingStationAPI = {
  getAllStations: () => axiosClient.get("/api/stations"),
  getStationByStatus: (status) => axiosClient.get(`/api/stations/status/${status}`),
  getNearbyStations: () => axiosClient.get("/api/stations/nearby"),
  getByConnectorType: (type) => axiosClient.get(`/api/stations/connector/${type}`),
};

export default chargingStationAPI;
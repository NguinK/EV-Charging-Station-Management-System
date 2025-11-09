import axiosClient from "./axiosClient";

const chargingStationAPI = {
  getAllStations: () => axiosClient.get("/api/stations/allStations"),
  getStationByStatus: (status) => axiosClient.get(`/api/stations/status/${status}`),
  getNearbyStations: () => axiosClient.get("/api/stations/nearby"),
  getByConnectorType: (type) => axiosClient.get(`/api/stations/connector/${type}`),
  createStation: (data) => axiosClient.post("/api/stations/createStation", data)
};

export default chargingStationAPI;
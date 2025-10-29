import axiosClient from "./axiosClient";

const reservationAPI = {
  createReservation: (data) => axiosClient.post("/drivers/reservations/auto", data),
  getReservationList: () => axiosClient.get("/drivers/reservations/getList"),
  getReservationDetails: (id) => axiosClient.get(`/drivers/reservations/getDetails/${id}`),
  deleteReservation: (id) => axiosClient.delete(`/drivers/reservations/deleteReservation/${id}`),
};

export default reservationAPI;

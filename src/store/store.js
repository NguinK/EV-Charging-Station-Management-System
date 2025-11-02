import { configureStore } from "@reduxjs/toolkit";
import reservationReducer from "../features/reservationSlice.js";

export const store = configureStore({
  reducer: {
    reservation: reservationReducer,
  },
});

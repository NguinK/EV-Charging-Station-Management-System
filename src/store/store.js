import { configureStore } from "@reduxjs/toolkit";
import reservationReducer from "../features/reservationSlice.js";
import sessionReducer  from "../features/sessionSlice.js";

export const store = configureStore({
  reducer: {
    reservation: reservationReducer,
    session: sessionReducer,
  },
});

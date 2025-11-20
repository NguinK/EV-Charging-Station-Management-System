import { configureStore } from "@reduxjs/toolkit";
import reservationReducer from "../features/reservationSlice.js";
import sessionReducer  from "../features/sessionSlice.js";
import walletReducer from "../features/walletSlice.js";
export const store = configureStore({
  reducer: {
    reservation: reservationReducer,
    session: sessionReducer,
     wallet: walletReducer,
  },
});

import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import chargingSessionAPI from "../api/chargingSessionAPI";

export const fetchSessionByReservation = createAsyncThunk(
  "session/fetchByReservation",
    async (reservationId) => {
    const res = await chargingSessionAPI.getByReservation(reservationId);
    return res.data;
  }
);
//
// sessionSlice.js
export const endSessionManual = createAsyncThunk(
  "session/endSessionManual",
   async (sessionId) => {
    const res = await chargingSessionAPI.endManual(sessionId);
    return res.data; // session sau khi kết thúc
  }
);

const sessionSlice = createSlice({
  name: "session",
  initialState: { info: null, loading: false, error: null },
  reducers: {
    clearSession: (state) => { state.info = null; },
    setSession: (state, action) => { state.info = action.payload; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchSessionByReservation.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchSessionByReservation.fulfilled, (state, action) => {
        state.loading = false;
        state.info = action.payload;
      })
      .addCase(fetchSessionByReservation.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(endSessionManual.fulfilled, (state, action) => {
        state.info = action.payload;
      });
  },
});

export const { clearSession, setSession } = sessionSlice.actions;
export default sessionSlice.reducer;

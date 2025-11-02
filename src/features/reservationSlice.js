import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import reservationAPI from "../api/reservationAPI.js";

// Thunk tạo mới đặt chỗ
export const startReservation = createAsyncThunk(
  "reservation/startReservation",
  async (payload, { rejectWithValue }) => {
    try {
      const res = await reservationAPI.createReservation(payload);
      return res.data;
    } catch (err) {
      return rejectWithValue(err.response?.data || err.message);
    }
  }
);

// Slice
const reservationSlice = createSlice({
  name: "reservation",
  initialState: {
    bookings: [],
    loading: false,
    error: null,
  },
  reducers: {
    resetError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(startReservation.pending, (state) => {
        state.loading = true;
      })
      .addCase(startReservation.fulfilled, (state, action) => {
        state.loading = false;
        state.bookings.unshift(action.payload); // thêm booking mới
      })
      .addCase(startReservation.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { resetError } = reservationSlice.actions;
export default reservationSlice.reducer;
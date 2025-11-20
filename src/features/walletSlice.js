import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import walletAPI from "../api/walletAPI";


export const depositToWallet = createAsyncThunk(
  "wallet/deposit",
  async ({ accountId, amount, description }, { rejectWithValue }) => {
    try {
      const res = await walletAPI.deposit(accountId, amount, description);
      return res.data;
    } catch (err) {
      return rejectWithValue(err?.response?.data || "Deposit failed");
    }
  }
);
export const fetchWalletBalance = createAsyncThunk(
  "wallet/fetchBalance",
  async (accountId, { rejectWithValue }) => {
    try {
      const res = await walletAPI.getWallet(accountId);
      // Tùy API trả về: log ra để chắc
      // console.log("Wallet data:", res.data);
      return res.data; // giả sử res.data = { id, balance, ... }
    } catch (err) {
      return rejectWithValue(err?.response?.data || "Fetch balance failed");
    }
  }
);
const walletSlice = createSlice({
  name: "wallet",
  initialState: { balance: null, loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(depositToWallet.pending, (state) => {
        state.loading = true;
      })
      .addCase(depositToWallet.fulfilled, (state, action) => {
        state.loading = false;
        state.balance = action.payload?.balanceAfter ?? state.balance;
      })
      .addCase(depositToWallet.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(fetchWalletBalance.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchWalletBalance.fulfilled, (state, action) => {
        state.loading = false;
        // giả sử API trả { balance: 100000, ... }
        state.balance = action.payload?.balance ?? state.balance;
      })
      .addCase(fetchWalletBalance.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default walletSlice.reducer;

import React from "react";
import {
  Box,
  Container,
  Typography,
  TextField,
  Button,
  Checkbox,
  FormControlLabel,
  Link,
  Grid,
  Stack,
  Paper,
} from "@mui/material";
import FacebookIcon from "@mui/icons-material/Facebook";
import GoogleIcon from "@mui/icons-material/Google";
import AppleIcon from "@mui/icons-material/Apple";

export default function LoginPage() {
  return (
    <Box
      sx={{
        minHeight: "100vh",
        bgcolor: "#111315",
        color: "#e7ffe7",
        background:
          "radial-gradient(1100px 560px at 78% 50%, rgba(56,210,111,0.12), transparent 60%)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontFamily: "Inter, system-ui, sans-serif",
      }}
    >
      <Container maxWidth="md">
        <Grid container spacing={4} alignItems="center" justifyContent="center">
          {/* Left section (form) */}
          <Grid item xs={12} md={6}>
            <Box sx={{ mb: 2, display: "flex", alignItems: "center", gap: 2 }}>
              <Box
                component="img"
                src="/ev-logo.png"
                alt="EV Station"
                sx={{ height: 48 }}
              />
              <Typography
                variant="h5"
                sx={{ fontWeight: 800, color: "#5af07e", letterSpacing: 0.2 }}
              >
                EV Station
              </Typography>
            </Box>

            <Typography variant="h6" sx={{ color: "#7cf69a", mb: 1 }}>
              Sign in
            </Typography>
            <Typography variant="body2" sx={{ color: "#8f9a99", mb: 2 }}>
              If you don’t have an account register <br />
              You can{" "}
              <Link href="#" sx={{ color: "#9cffb7" }}>
                Register here!
              </Link>
            </Typography>

            <Stack spacing={2}>
              <TextField
                label="Email"
                placeholder="Enter your email address"
                variant="outlined"
                fullWidth
                InputProps={{
                  sx: {
                    bgcolor: "#1a1d1f",
                    borderRadius: "10px",
                    color: "#e9ffee",
                  },
                }}
                InputLabelProps={{ sx: { color: "#b6c6bf" } }}
              />

              <TextField
                label="Password"
                placeholder="Enter your password"
                type="password"
                variant="outlined"
                fullWidth
                InputProps={{
                  sx: {
                    bgcolor: "#1a1d1f",
                    borderRadius: "10px",
                    color: "#e9ffee",
                  },
                }}
                InputLabelProps={{ sx: { color: "#b6c6bf" } }}
              />

              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <FormControlLabel
                  control={
                    <Checkbox
                      sx={{
                        color: "#34c759",
                        "&.Mui-checked": { color: "#34c759" },
                      }}
                    />
                  }
                  label="Remember me"
                  sx={{ color: "#cfd8d3", fontSize: 12 }}
                />
                <Link href="#" sx={{ color: "#a6ffbe", fontSize: 12 }}>
                  Forgot password?
                </Link>
              </Box>

              <Button
                variant="contained"
                fullWidth
                sx={{
                  mt: 1,
                  py: 1.5,
                  borderRadius: "999px",
                  background: "linear-gradient(90deg,#2cd06d,#49eb85)",
                  color: "#06240f",
                  fontWeight: 700,
                  "&:hover": {
                    background: "linear-gradient(90deg,#34c759,#45e47d)",
                  },
                }}
              >
                Login
              </Button>

              <Typography
                align="center"
                sx={{ color: "#9ab1a7", fontSize: 13, mt: 2 }}
              >
                or continue with
              </Typography>

              <Stack direction="row" spacing={2} justifyContent="center">
                <Paper
                  elevation={3}
                  sx={{
                    width: 42,
                    height: 42,
                    borderRadius: "50%",
                    bgcolor: "#0e1111",
                    border: "1px solid #283030",
                    display: "grid",
                    placeItems: "center",
                    cursor: "pointer",
                  }}
                >
                  <FacebookIcon sx={{ color: "#1877F2" }} />
                </Paper>
                <Paper
                  elevation={3}
                  sx={{
                    width: 42,
                    height: 42,
                    borderRadius: "50%",
                    bgcolor: "#0e1111",
                    border: "1px solid #283030",
                    display: "grid",
                    placeItems: "center",
                    cursor: "pointer",
                  }}
                >
                  <AppleIcon sx={{ color: "#e5e7eb" }} />
                </Paper>
                <Paper
                  elevation={3}
                  sx={{
                    width: 42,
                    height: 42,
                    borderRadius: "50%",
                    bgcolor: "#0e1111",
                    border: "1px solid #283030",
                    display: "grid",
                    placeItems: "center",
                    cursor: "pointer",
                  }}
                >
                  <GoogleIcon sx={{ color: "#34A853" }} />
                </Paper>
              </Stack>
            </Stack>
          </Grid>

          {/* Right section (image) */}
          <Grid item xs={12} md={6}>
            <Box
              sx={{
                bgcolor: "#0a0b0c",
                borderRadius: 2,
                border: "1px solid #1e2323",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                p: 2,
              }}
            >
              <Box
                component="img"
                src="/ev-login-visual.png"
                alt="EV Station login visual"
                sx={{
                  maxWidth: "92%",
                  filter: "drop-shadow(0 40px 60px rgba(0,0,0,0.6))",
                  borderRadius: 1,
                }}
              />
            </Box>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}

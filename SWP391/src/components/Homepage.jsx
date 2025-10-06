import React from "react";
import {
  Box,
  Container,
  Toolbar,
  Typography,
  Button,
  Stack,
} from "@mui/material";
import EVHeader from "../layout/Header"; // dùng lại header cũ
import { EVFooter } from "../layout/Footer"; // dùng lại footer

export default function Homepage() {
  return (
    <>
      {/* Header trên cùng */}
      <EVHeader />
      <Toolbar /> {/* spacer cho fixed header */}
      {/* Nội dung chính */}
      <Box
        sx={{
          bgcolor: "#0f0f0f",
          color: "#eaeaea",
          py: 6,
          minHeight: "calc(100vh - 64px - 56px)", // header 64 + footer ~56
          width: "100%",
        }}
      >
        <Container maxWidth="lg">
          <Stack
            direction="row"
            spacing={6}
            alignItems="center"
            justifyContent="center"
          >
            {/* Ảnh trụ sạc hoặc xe */}
            <Box
              component="img"
              src="/charger.png"
              alt="Charger"
              sx={{ width: 220, borderRadius: 2 }}
            />

            {/* Nội dung giới thiệu */}
            <Box sx={{ textAlign: "center" }}>
              <Box
                component="img"
                src="/ev_logo.png"
                alt="EV icon"
                sx={{ width: 38, mb: 1 }}
              />
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 1 }}>
                About EV Station
              </Typography>
              <Typography
                variant="body2"
                sx={{ opacity: 0.9, mb: 2, maxWidth: 520, mx: "auto" }}
              >
                EV Station is a leading company in providing high-quality and
                reliable EV charging solutions. Our stations support CCS,
                CHAdeMO, and AC. All products comply with European standards,
                certified by TÜV/ICR.
              </Typography>
              <Button
                variant="contained"
                sx={{
                  background: "#34c759",
                  ":hover": { background: "#2eb24f" },
                  borderRadius: 999,
                  px: 3,
                }}
              >
                CHARGE NOW
              </Button>
            </Box>
          </Stack>
        </Container>
      </Box>
      {/* Footer dưới cùng */}
      <EVFooter />
    </>
  );
}

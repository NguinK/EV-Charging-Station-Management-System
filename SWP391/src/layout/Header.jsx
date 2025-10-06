import React from "react";
import { AppBar, Toolbar, Box, Button, Typography, Stack } from "@mui/material";

const navItems = [
  { label: "Home", href: "/" },
  { label: "Charge", href: "/charge" },
  { label: "Price", href: "/price" },
  { label: "Login", href: "/login", isLogin: true },
];

export default function EVHeader() {
  return (
    <AppBar
      position="fixed"
      elevation={0}
      sx={{
        top: 0,
        left: 0,
        right: 0,
        background: "#e5e5e5",
        color: "#111",
        borderRadius: 0,
        px: { xs: 2, sm: 4 },
      }}
    >
      <Toolbar sx={{ minHeight: 64, gap: 2 }}>
        {/* Logo + Brand */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 1.25 }}>
          <Box
            component="img"
            src="/ev_logo.png"
            alt="EV Station"
            sx={{ width: 30, height: 30, objectFit: "contain" }}
          />
          <Typography
            variant="h6"
            sx={{
              fontWeight: 700,
              letterSpacing: 0.25,
              background: "linear-gradient(90deg, #9cd6a8, #2ecc71)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
            }}
          >
            EV Station
          </Typography>
        </Box>

        {/* Nav items chia đều */}
        <Stack
          direction="row"
          sx={{ flexGrow: 1, justifyContent: "space-evenly" }}
          alignItems="center"
        >
          {navItems.map((item) =>
            item.isLogin ? (
              <Button
                key={item.label}
                href={item.href}
                variant="contained"
                disableElevation
                sx={{
                  textTransform: "none",
                  fontWeight: 600,
                  borderRadius: 999,
                  px: 2.5,
                  backgroundColor: "#34c759",
                  ":hover": { backgroundColor: "#2eb24f" },
                }}
              >
                {item.label}
              </Button>
            ) : (
              <Button
                key={item.label}
                href={item.href}
                variant="text"
                sx={{
                  textTransform: "none",
                  fontSize: 16,
                  fontWeight: 500,
                  color: "#111",
                }}
              >
                {item.label}
              </Button>
            )
          )}
        </Stack>
      </Toolbar>
    </AppBar>
  );
}

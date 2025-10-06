import React from "react";
import { Box, Container, Typography } from "@mui/material";

export function EVFooter() {
  return (
    <Box
      component="footer"
      sx={{
         width: "100%",           // full-width theo viewport
        bgcolor: "#1f1f1f",
        color: "#eaeaea",
        textAlign: "center",
        py: 2,
        mt: 0,
      }}
    >
      
        <Typography variant="body2">Copyright 2025 ©EVStation</Typography>
      
    </Box>
  );
}
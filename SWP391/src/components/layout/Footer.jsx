import React from "react";
import { Box, Container, Typography } from "@mui/material";
import { Footer } from "antd/es/layout/layout";

export function EVFooter() {
  return (
     <Footer style={{ textAlign: 'center' }}>
          EVStation ©{new Date().getFullYear()} 
        </Footer>
  );
}
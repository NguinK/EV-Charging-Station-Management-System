import React from "react";
import {
  Box,
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Stack,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Slide,
} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";

export default function PricePage() {
  const [show, setShow] = React.useState(false);

  React.useEffect(() => {
    const t = setTimeout(() => setShow(true), 100);
    return () => clearTimeout(t);
  }, []);

  return (
    <Box
      sx={{
        bgcolor: "#0f0f0f",
        color: "#eaeaea",
        py: 6,
        minHeight: "calc(100vh - 64px - 56px)", // chừa header+footer
        width: "100%",
      }}
    >
      <Container maxWidth="lg">
        <Typography
          variant="h4"
          align="center"
          sx={{ fontWeight: 800, letterSpacing: 1, mb: 6 }}
        >
          PRICE
        </Typography>

        <Grid container spacing={6} justifyContent="center">
          {/* Gói One Time */}
          <Grid item xs={12} md={5}>
            <Slide in={show} direction="up" timeout={500}>
              <Card
                sx={{
                  bgcolor: "#121212",
                  color: "#eaeaea",
                  borderRadius: 2,
                  boxShadow: 4,
                  border: "1px solid #2b2b2b",
                }}
              >
                <CardContent>
                  <Typography
                    align="center"
                    sx={{ fontWeight: 800, opacity: 0.9, mb: 2 }}
                  >
                    ONE TIME
                  </Typography>
                  <Typography
                    align="center"
                    sx={{ fontSize: 36, fontWeight: 900, mb: 3 }}
                  >
                    9.000Đ/KWH
                  </Typography>

                  <Stack alignItems="center" sx={{ mb: 3 }}>
                    <Button
                      variant="contained"
                      disableElevation
                      sx={{
                        background: "#34c759",
                        ":hover": { background: "#2eb24f" },
                        borderRadius: 1.2,
                        px: 3,
                      }}
                    >
                      Get Started
                    </Button>
                  </Stack>

                  <List dense>
                    {["All station", "Booking Free"].map((text) => (
                      <ListItem key={text} sx={{ py: 0.75 }}>
                        <ListItemIcon sx={{ minWidth: 32 }}>
                          <CheckCircleIcon
                            fontSize="small"
                            sx={{ color: "#34c759" }}
                          />
                        </ListItemIcon>
                        <ListItemText
                          primaryTypographyProps={{ fontSize: 14 }}
                          primary={text}
                        />
                      </ListItem>
                    ))}
                  </List>
                </CardContent>
              </Card>
            </Slide>
          </Grid>

          {/* Gói Subscription */}
          <Grid item xs={12} md={5}>
            <Slide in={show} direction="up" timeout={700}>
              <Card
                sx={{
                  bgcolor: "#121212",
                  color: "#eaeaea",
                  borderRadius: 2,
                  boxShadow: 4,
                  border: "1px solid #2b2b2b",
                }}
              >
                <CardContent>
                  <Typography
                    align="center"
                    sx={{ fontWeight: 800, opacity: 0.9, mb: 2 }}
                  >
                    SUBSCRIPTION
                  </Typography>
                  <Typography
                    align="center"
                    sx={{ fontSize: 36, fontWeight: 900, mb: 0 }}
                  >
                    1.000.000 VNĐ
                  </Typography>
                  <Typography align="center" sx={{ opacity: 0.7, mb: 3 }}>
                    Per Year
                  </Typography>

                  <Stack alignItems="center" sx={{ mb: 3 }}>
                    <Button
                      variant="contained"
                      disableElevation
                      sx={{
                        background: "#34c759",
                        ":hover": { background: "#2eb24f" },
                        borderRadius: 1.2,
                        px: 3,
                      }}
                    >
                      Get Started
                    </Button>
                  </Stack>

                  <List dense>
                    {[
                      "All station",
                      "Booking Free",
                      "4200 VND / KWH",
                      "Prioritize fast charging types",
                      "No waiting",
                    ].map((text) => (
                      <ListItem key={text} sx={{ py: 0.75 }}>
                        <ListItemIcon sx={{ minWidth: 32 }}>
                          <CheckCircleIcon
                            fontSize="small"
                            sx={{ color: "#34c759" }}
                          />
                        </ListItemIcon>
                        <ListItemText
                          primaryTypographyProps={{ fontSize: 14 }}
                          primary={text}
                        />
                      </ListItem>
                    ))}
                  </List>
                </CardContent>
              </Card>
            </Slide>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}

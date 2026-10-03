import { createTheme } from "@mui/material/styles";

export const temaMui = createTheme({
  palette: {
    primary: {
      main: "#22603f",
      dark: "#1b4a31",
      contrastText: "#ffffff",
    },
    success: {
      main: "#2a7a4e",
    },
    error: {
      main: "#b3392b",
    },
    text: {
      primary: "#17211b",
      secondary: "#5e6a62",
    },
    background: {
      default: "#f1f5ee",
    },
  },
  typography: {
    fontFamily: '"Work Sans", system-ui, sans-serif',
    button: {
      textTransform: "none",
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 999,
          boxShadow: "none",
          "&:hover": { boxShadow: "none" },
        },
      },
    },
    MuiAlert: {
      styleOverrides: {
        root: {
          borderRadius: 14,
        },
      },
    },
  },
});

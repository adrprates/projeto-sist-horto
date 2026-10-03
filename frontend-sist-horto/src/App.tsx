import { ThemeProvider } from '@mui/material/styles';
import { AuthProvider } from './context/AuthProvider';
import { AppRoutes } from './routes/AppRoutes';
import { temaMui } from './config/temaMui';
import './App.css'

function App() {
  return (
    <ThemeProvider theme={temaMui}>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;

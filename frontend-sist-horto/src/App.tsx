import { AuthProvider } from './context/AuthProvider';
import { AppRoutes } from './routes/AppRoutes';
import './App.css'
import { useEffect } from 'react';

function App() {
  useEffect(() => {
    document.title = "Sistema Reserva de Mudas";
  }, []);

  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  );
}

export default App;
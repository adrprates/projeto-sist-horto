import { Box, Button, Container, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

export const PaginaNotFound = () => {
  return (
    <Container>
      <Box
        sx={{
          py: 12,
          maxWidth: 480,
          mx: 'auto',
          display: 'flex',
          minHeight: '60vh',
          textAlign: 'center',
          alignItems: 'center',
          flexDirection: 'column',
          justifyContent: 'center',
        }}
      >
        <Typography variant="h3" component="h1" sx={{ mb: 2 }}>
          Página Não Encontrada!
        </Typography>
        <Typography sx={{ color: 'text.secondary' }}>
          Desculpe, não conseguimos encontrar a página que você está procurando.
        </Typography>
        
        <Box
          component="img"
          src="/assets/illustrations/illustration_404.svg" // Adicione uma imagem 404 em public/assets se desejar
          sx={{ height: 260, mx: 'auto', my: { xs: 5, sm: 10 } }}
        />

        <Button to="/" size="large" variant="contained" component={RouterLink}>
          Voltar para o Início
        </Button>
      </Box>
    </Container>
  );
};
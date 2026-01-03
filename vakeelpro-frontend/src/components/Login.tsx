import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  Avatar,
  Box,
  Button,
  Container,
  Paper,
  TextField,
  Typography,
  Alert
} from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';

const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const mockToken =
      'eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJzYWlrb3RpIHJvbmdhbGkiLCJpYXQiOjE3NDM4MzE5MzgsImV4cCI6MTc0Mzg2NzkzOCwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlcyI6WyJVU0VSIl0sImZlYXR1cmVQcml2aWxlZ2VzIjp7Ik1BU1RFUkRBVEFNQU5BR0VNRU5UIjpbXSwiVVNFUlBST0ZJTEUiOltdLCJEQVNIIjp7fX19.kUOpVbhftX3HcI4Dw6xyWKtPMx-ksB9JLOq-Ke4Crlf9y2r_vyvj2MDbFCFAzXLm';

    try {
      login(mockToken);
    } catch (err) {
      setError('Login failed. Please try again.');
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f5f5f5',
      }}
    >
      <Container maxWidth="xs">
        <Paper
          elevation={6}
          sx={{
            p: 4,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            borderRadius: 2,
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: 'primary.main' }}>
            <LockOutlinedIcon />
          </Avatar>

          <Typography component="h1" variant="h5" sx={{ mb: 2 }}>
            Login
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2, width: '100%' }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1, width: '100%' }}>
            <TextField
              margin="normal"
              fullWidth
              label="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoFocus
            />

            <TextField
              margin="normal"
              fullWidth
              type="password"
              label="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Login
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default Login;

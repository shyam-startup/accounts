import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import { getMe } from './api/auth'
import './App.css'

function RootRedirect() {
  const navigate = useNavigate()

  useEffect(() => {
    getMe()
      .then(() => navigate('/dashboard', { replace: true }))
      .catch(() => navigate('/login', { replace: true }))
  }, [])

  return null
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RootRedirect />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="*" element={<RootRedirect />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App

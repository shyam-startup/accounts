import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { signin, authorize, getMe } from '../api/auth'

function EyeIcon({ open }) {
  return open ? (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  ) : (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
      <line x1="1" y1="1" x2="23" y2="23" />
    </svg>
  )
}

function Login() {
  const navigate = useNavigate()
  const clientId = new URLSearchParams(window.location.search).get('client_id')
  const [form, setForm] = useState({ username: '', password: '' })
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [checking, setChecking] = useState(true)
  const [alreadyLoggedIn, setAlreadyLoggedIn] = useState(false)

  useEffect(() => {
    getMe()
      .then(async () => {
        if (clientId) {
          try {
            const { data } = await authorize(clientId)
            window.location.href = data.redirectUrl
          } catch {
            setAlreadyLoggedIn(true)
            setTimeout(() => navigate('/dashboard', { replace: true }), 2000)
          }
        } else {
          setAlreadyLoggedIn(true)
          setTimeout(() => navigate('/dashboard', { replace: true }), 2000)
        }
      })
      .catch(() => setChecking(false))
  }, [])

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.id]: e.target.value }))
    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.username || !form.password) {
      setError('Please fill in all fields.')
      return
    }
    try {
      setLoading(true)
      await signin({ username: form.username, password: form.password })
      if (clientId) {
        const { data } = await authorize(clientId)
        window.location.href = data.redirectUrl
      } else {
        navigate('/dashboard', { replace: true })
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-layout">
      {(checking || alreadyLoggedIn) && (
        <div className="auth-check-overlay">
          {alreadyLoggedIn ? (
            <div className="auth-check-popup">
              <div className="auth-check-icon">✓</div>
              <p className="auth-check-title">Already logged in</p>
              <p className="auth-check-sub">Redirecting you to the dashboard…</p>
            </div>
          ) : clientId ? (
            <div className="auth-check-popup">
              <div className="spinner" />
              <p className="auth-check-sub">Signing you in…</p>
            </div>
          ) : (
            <div className="auth-check-popup">
              <div className="spinner" />
            </div>
          )}
        </div>
      )}
      <div className="login-brand">
        <div className="brand-content">
          <div className="brand-logo">
            <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg>
          </div>
          <h2 className="brand-name">Accounts SSO</h2>
          <p className="brand-tagline">One login. Every app.</p>
          <ul className="brand-features">
            <li>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12" /></svg>
              Secure single sign-on
            </li>
            <li>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12" /></svg>
              Session management
            </li>
            <li>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12" /></svg>
              Role-based access control
            </li>
          </ul>
        </div>
        <p className="brand-footer">© 2025 Accounts SSO</p>
      </div>

      <div className="login-form-panel">
        <div className="login-form-inner">
          <div className="form-header">
            <h1>Welcome back</h1>
            <p className="subtitle">Enter your credentials to continue</p>
          </div>

          {error && <div className="alert-error">{error}</div>}

          <form className="login-form" onSubmit={handleSubmit} noValidate>
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <div className="input-wrapper">
                <svg className="input-icon" xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" />
                </svg>
                <input
                  id="username"
                  type="text"
                  placeholder="Enter your username"
                  autoComplete="username"
                  value={form.username}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-group">
              <div className="label-row">
                <label htmlFor="password">Password</label>
                <a href="#" className="forgot-link">Forgot password?</a>
              </div>
              <div className="input-wrapper">
                <svg className="input-icon" xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" /><path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  value={form.password}
                  onChange={handleChange}
                />
                <button type="button" className="toggle-password" onClick={() => setShowPassword(v => !v)} aria-label={showPassword ? 'Hide password' : 'Show password'}>
                  <EyeIcon open={showPassword} />
                </button>
              </div>
            </div>

            <label className="checkbox-label">
              <input type="checkbox" />
              <span className="checkbox-custom" />
              Keep me signed in
            </label>

            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? <span className="spinner" /> : 'Sign In'}
            </button>
          </form>

          <p className="auth-footer">
            Don&apos;t have an account? <Link to="/register">Create one</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login

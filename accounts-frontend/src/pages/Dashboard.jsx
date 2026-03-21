import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMe, signout } from '../api/auth'
import './Dashboard.css'

function Dashboard() {
  const navigate = useNavigate()
  const [user, setUser]   = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getMe()
      .then(({ data }) => setUser(data))
      .catch(() => navigate('/login', { replace: true }))
      .finally(() => setLoading(false))
  }, [navigate])

  const handleSignOut = async () => {
    await signout().catch(() => {})
    navigate('/login', { replace: true })
  }

  if (loading) {
    return (
      <div className="dashboard">
        <div className="dashboard-loading">
          <span className="spinner-dark" />
        </div>
      </div>
    )
  }

  if (!user) return null

  return (
    <div className="dashboard">
      <div className="dashboard-card">
        <div className="dashboard-avatar">
          {user.username.charAt(0).toUpperCase()}
        </div>
        <h1 className="dashboard-greeting">Hello, {user.username} 👋</h1>
        <p className="dashboard-email">{user.email}</p>
        <div className="dashboard-meta">
          <div className="meta-item">
            <span className="meta-label">User ID</span>
            <span className="meta-value">{user.userId}</span>
          </div>
        </div>
        <button className="btn-signout" onClick={handleSignOut}>
          Sign Out
        </button>
      </div>
    </div>
  )
}

export default Dashboard

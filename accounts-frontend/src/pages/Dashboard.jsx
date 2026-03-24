import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMe, signout, getConnectedApps, getAvailableApps } from '../api/auth'
import './Dashboard.css'

function ShieldIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
    </svg>
  )
}

function UserIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  )
}

function KeyIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="7.5" cy="15.5" r="5.5" />
      <path d="M21 2l-9.6 9.6M15.5 7.5l3 3" />
    </svg>
  )
}

function LogoutIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <polyline points="16 17 21 12 16 7" />
      <line x1="21" y1="12" x2="9" y2="12" />
    </svg>
  )
}

function CheckIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="20 6 9 17 4 12" />
    </svg>
  )
}

function Dashboard() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)
  const [apps, setApps] = useState([])
  const [availableApps, setAvailableApps] = useState([])
  const [loading, setLoading] = useState(true)
  const [signingOut, setSigningOut] = useState(false)

  useEffect(() => {
    Promise.all([getMe(), getConnectedApps(), getAvailableApps()])
      .then(([meRes, appsRes, availableRes]) => {
        setUser(meRes.data)
        setApps(appsRes.data)
        setAvailableApps(availableRes.data)
      })
      .catch(() => navigate('/login', { replace: true }))
      .finally(() => setLoading(false))
  }, [navigate])

  const handleSignOut = async () => {
    setSigningOut(true)
    await signout().catch(() => {})
    navigate('/login', { replace: true })
  }

  if (loading) {
    return (
      <div className="db-loading">
        <span className="spinner-dark" />
      </div>
    )
  }

  if (!user) return null

  const initials = [user.firstName, user.lastName]
    .filter(Boolean)
    .map(n => n.charAt(0).toUpperCase())
    .join('') || user.username.charAt(0).toUpperCase()
  const fullName = [user.firstName, user.lastName].filter(Boolean).join(' ') || user.username

  return (
    <div className="db-root">
      {/* Sidebar */}
      <aside className="db-sidebar">
        <div className="db-sidebar-logo">
          <ShieldIcon />
          <span>Accounts SSO</span>
        </div>

        <nav className="db-nav">
          <div className="db-nav-item active">
            <UserIcon />
            <span>Profile</span>
          </div>
        </nav>

        <div className="db-sidebar-footer">
          <div className="db-avatar db-avatar-sm">{initials}</div>
          <button className="db-signout-btn" onClick={handleSignOut} disabled={signingOut} title="Sign out">
            <LogoutIcon />
          </button>
        </div>
      </aside>

      {/* Main */}
      <main className="db-main">
        {/* Top bar */}
        <header className="db-topbar">
          <div>
            <h1 className="db-topbar-title">My Profile</h1>
            <p className="db-topbar-sub">Manage your account details</p>
          </div>
          <div className="db-status-pill">
            <span className="db-status-dot" />
            Session active
          </div>
        </header>

        {/* Content */}
        <div className="db-content">
          <div className="db-columns">
            {/* Left column */}
            <div className="db-col-left">
              {/* Profile card */}
              <div className="db-profile-card">
                <div className="db-avatar db-avatar-lg">{initials}</div>
                <div className="db-profile-info">
                  <h2 className="db-profile-name">{fullName}</h2>
                </div>
                {user.confirmed ? (
                  <div className="db-verified-badge">
                    <CheckIcon />
                    Verified
                  </div>
                ) : (
                  <div className="db-unverified-badge">
                    Not Verified
                  </div>
                )}
              </div>

              {/* Detail table */}
              <div className="db-detail-table-wrap">
                <table className="db-detail-table">
                <tbody>
                  {[
                    { label: 'First Name', value: user.firstName || '—' },
                    { label: 'Last Name',  value: user.lastName  || '—' },
                    { label: 'Username',   value: '@' + user.username },
                    { label: 'Email',      value: user.email },
                  ].map(({ label, value }) => (
                    <tr key={label}>
                      <td className="db-table-label">{label}</td>
                      <td className="db-table-value">{value}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              </div>
            </div>

            {/* Right column */}
            <div className="db-col-right">
              <div className="db-section">
                <h3 className="db-section-title">Apps</h3>
                {availableApps.length === 0 ? (
                  <div className="db-empty-state">
                    <ShieldIcon />
                    <p>No apps available</p>
                    <span>Apps registered with this SSO will appear here</span>
                  </div>
                ) : (
                  <div className="db-apps-grid">
                    {availableApps.map(app => {
                      const connected = apps.some(a => a.clientId === app.clientId)
                      const connectedApp = apps.find(a => a.clientId === app.clientId)
                      return (
                        <div className="db-app-card" key={app.clientId}>
                          <div className="db-app-card-avatar">
                            {app.clientName.charAt(0).toUpperCase()}
                          </div>
                          <div className="db-app-card-info">
                            <span className="db-app-card-name">{app.clientName}</span>
                            {connected && (
                              <span className="db-app-card-since">
                                Since {new Date(connectedApp.consentedAt).toLocaleDateString()}
                              </span>
                            )}
                          </div>
                          <span className={`db-app-card-tag ${connected ? 'db-tag-connected' : 'db-tag-install'}`}>
                            {connected ? 'Connected' : 'Install'}
                          </span>
                        </div>
                      )
                    })}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}

export default Dashboard


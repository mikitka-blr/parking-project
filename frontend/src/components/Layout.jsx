import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { Car } from 'lucide-react';
import api from '../api/api';

export default function Layout() {
    const isAdmin = localStorage.getItem('isAdmin') === 'true';
    const logoTarget = isAdmin ? '/app' : '/app/reservations';
    return (
        <>
            <header className="header">
                <div className="header-container">
                    <Link to={logoTarget} className="logo" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <Car size={28} />
                        AutoParking.by
                    </Link>
                    <nav className="nav-links">
                        {isAdmin && <Link to="/app" className="nav-link">Пользователи</Link>}
                        <Link to="/app/slots" className="nav-link">Парковка</Link>
                        <Link to="/app/reservations" className="nav-link">Бронирования</Link>
                    </nav>
                </div>
            </header>

            <main className="main-content">
                <div className="container">
                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginBottom: 10 }}>
                        {/* Показываем текущего пользователя/админа и кнопку выхода */}
                        <CurrentUserInfo />
                    </div>
                    <Outlet />
                </div>
            </main>
        </>
    );
}

function CurrentUserInfo() {
    const navigate = useNavigate();
    const [name, setName] = React.useState('Гость');
    const [isAdmin, setIsAdmin] = React.useState(false);

    React.useEffect(() => {
        const admin = localStorage.getItem('isAdmin') === 'true';
        setIsAdmin(admin);
        const uid = localStorage.getItem('currentUserId');
        if (uid) {
            api.get(`/users/${uid}`).then(r => { if (r.data && r.data.fullName) setName(r.data.fullName); }).catch(() => {});
        } else if (admin) {
            setName('Admin');
        }
    }, []);

    const logout = () => {
        localStorage.removeItem('isAdmin');
        localStorage.removeItem('currentUserId');
        navigate('/');
    };

    return (
        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <span style={{ color: '#555' }}>{isAdmin ? 'Admin' : name}</span>
            <button className="btn" onClick={logout} style={{ padding: '6px 10px' }}>Выйти</button>
        </div>
    );
}


import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Auth.css';

const AuthForm = () => {
    const [isLoginMode, setIsLoginMode] = useState(true);
    const [currentCaptchaText, setCurrentCaptchaText] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [fullName, setFullName] = useState('');
    const [captchaInput, setCaptchaInput] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate(); // Keep navigate for the temporary button if needed, but not for login success

    const generateCaptcha = () => {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let result = '';
        for (let i = 0; i < 6; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        setCurrentCaptchaText(result);
        setCaptchaInput(''); // Clear previous input
    };

    useEffect(() => {
        generateCaptcha();
    }, []);

    const validateEmail = (email) => {
        return /^[a-zA-Z0-9_.-]+@srmist\.edu\.in$/.test(email);
    };

    const validatePassword = (password) => {
        return password.length >= 6;
    };

    const handleSwitchMode = (e) => {
        e.preventDefault();
        setIsLoginMode(!isLoginMode);
        setErrorMessage('');
        setSuccessMessage('');
        generateCaptcha();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setSuccessMessage('');

        if (captchaInput !== currentCaptchaText) {
            setErrorMessage('CAPTCHA verification failed. Please try again.');
            generateCaptcha();
            return;
        }

        if (!validateEmail(email)) {
            setErrorMessage('Invalid email format or domain. Must be @srmist.edu.in');
            return;
        }

        if (!validatePassword(password)) {
            setErrorMessage('Password must be at least 6 characters long.');
            return;
        }

        const endpoint = isLoginMode
            ? "http://localhost:8080/api/auth/login"
            : "http://localhost:8080/api/auth/register";

        const payload = isLoginMode
            ? { email, password }
            : { email, password, fullName };

        try {
            const response = await fetch(endpoint, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            const data = await response.json();

            if (response.ok) {
                setSuccessMessage(data.message || (isLoginMode ? "Login successful!" : "Registration successful!"));
                console.log('Login response data:', data);
                console.log('AuthForm: isLoginMode is', isLoginMode);

                if (isLoginMode && data.sessionToken) {
                    localStorage.setItem("sessionToken", data.sessionToken);
                    navigate('/dashboard');
                } else if (!isLoginMode) {
                    setIsLoginMode(true); // Switch to login mode after successful registration
                    setEmail('');
                    setPassword('');
                    setFullName('');
                }
            } else {
                setErrorMessage(data.message || data.error || (isLoginMode ? "Invalid credentials!" : "Registration failed!"));
            }
        } catch (err) {
            console.error("Fetch error:", err);
            setErrorMessage("❌ Failed to connect to backend.");
        } finally {
            generateCaptcha(); // Generate new CAPTCHA after submission
        }
    };

    const handleGoToDashboard = () => {
        navigate('/dashboard');
    };

    return (
        <div className="login-card">
            <h2 className="login-title">PlanWise {isLoginMode ? 'Login' : 'Sign Up'}</h2>

            {errorMessage && <div className="error-message" style={{ display: 'block' }}>{errorMessage}</div>}
            {successMessage && <div className="success-message" style={{ display: 'block' }}>{successMessage}</div>}

            <form onSubmit={handleSubmit}>
                {!isLoginMode && (
                    <div className="form-group fullname-form-group">
                        <label htmlFor="fullName">Full Name</label>
                        <input
                            type="text"
                            id="fullName"
                            name="fullName"
                            placeholder="Full Name"
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                        />
                    </div>
                )}
                <div className="form-group email-form-group">
                    <label htmlFor="email">SRM Email</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        required
                        placeholder="user@srmist.edu.in"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
                <div className="form-group password-form-group">
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        required
                        placeholder="min 6 characters"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="captcha">CAPTCHA: <span className="captcha-text">{currentCaptchaText}</span></label>
                    <input
                        type="text"
                        id="captcha"
                        name="captcha"
                        required
                        placeholder="Enter CAPTCHA"
                        value={captchaInput}
                        onChange={(e) => setCaptchaInput(e.target.value)}
                    />
                    <button type="button" onClick={generateCaptcha} style={{ marginTop: '0.5rem', padding: '0.5rem', borderRadius: '5px', border: '1px solid #ccc', background: '#f0f0f0', cursor: 'pointer' }}>Refresh CAPTCHA</button>
                </div>
                <button type="submit" className="login-btn">{isLoginMode ? 'Log In' : 'Register'}</button>
            </form>

            <div className="login-footer">
                {isLoginMode ? (
                    <>Don’t have an account? <button type="button" onClick={handleSwitchMode} style={{ background: 'none', border: 'none', color: '#37897e', textDecoration: 'underline', fontWeight: '500', cursor: 'pointer', padding: '0', fontSize: 'inherit' }}>Sign Up</button></>
                ) : (
                    <>Already have an account? <button type="button" onClick={handleSwitchMode} style={{ background: 'none', border: 'none', color: '#37897e', textDecoration: 'underline', fontWeight: '500', cursor: 'pointer', padding: '0', fontSize: 'inherit' }}>Log In</button></>
                )}
            </div>

        </div>
    );
};

export default AuthForm;

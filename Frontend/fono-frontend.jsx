import React, { useState, useEffect, useRef } from 'react';

// ============================================
// 🎨 FONO FLOW - Frontend de Fonoaudiologia
// ============================================

const API_URL = 'http://localhost:8080';

// ============================================
// 🎯 COMPONENTE PRINCIPAL - APP
// ============================================
export default function FonoApp() {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [currentView, setCurrentView] = useState('login');

    useEffect(() => {
        const savedUser = localStorage.getItem('user');
        if (savedUser && token) {
            setUser(JSON.parse(savedUser));
            setCurrentView('dashboard');
        }
    }, [token]);

    const handleLogin = (userData, tokenData) => {
        setUser(userData);
        setToken(tokenData);
        localStorage.setItem('token', tokenData);
        localStorage.setItem('user', JSON.stringify(userData));
        setCurrentView('dashboard');
    };

    const handleLogout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setCurrentView('login');
    };

    return (
        <div style={styles.app}>
            {/* Background animado */}
            <div style={styles.bgPattern} />

            {currentView === 'login' && (
                <LoginScreen onLogin={handleLogin} />
            )}

            {currentView === 'dashboard' && user && (
                <Dashboard
                    user={user}
                    token={token}
                    onLogout={handleLogout}
                    onStartChat={() => setCurrentView('chat')}
                />
            )}

            {currentView === 'chat' && user && (
                <ChatSession
                    user={user}
                    token={token}
                    onBack={() => setCurrentView('dashboard')}
                />
            )}
        </div>
    );
}

// ============================================
// 🔐 TELA DE LOGIN
// ============================================
function LoginScreen({ onLogin }) {
    const [login, setLogin] = useState('');
    const [senha, setSenha] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [showPassword, setShowPassword] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ login, senha })
            });

            if (!response.ok) {
                throw new Error('Credenciais inválidas');
            }

            const data = await response.json();
            onLogin({
                id: data.id,
                nome: data.nome,
                tipo: data.tipoUsuario
            }, data.token);

        } catch (err) {
            setError(err.message || 'Erro ao fazer login');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.loginContainer}>
            <div style={styles.loginCard}>
                {/* Logo / Header */}
                <div style={styles.loginHeader}>
                    <div style={styles.logoIcon}>🗣️</div>
                    <h1 style={styles.loginTitle}>FonoFlow</h1>
                    <p style={styles.loginSubtitle}>Treino de pronúncia com IA</p>
                </div>

                {/* Formulário */}
                <form onSubmit={handleSubmit} style={styles.loginForm}>
                    <div style={styles.inputGroup}>
                        <label style={styles.inputLabel}>Login</label>
                        <div style={styles.inputWrapper}>
                            <span style={styles.inputIcon}>👤</span>
                            <input
                                type="text"
                                value={login}
                                onChange={(e) => setLogin(e.target.value)}
                                placeholder="Digite seu login"
                                style={styles.input}
                                required
                            />
                        </div>
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.inputLabel}>Senha</label>
                        <div style={styles.inputWrapper}>
                            <span style={styles.inputIcon}>🔒</span>
                            <input
                                type={showPassword ? 'text' : 'password'}
                                value={senha}
                                onChange={(e) => setSenha(e.target.value)}
                                placeholder="Digite sua senha"
                                style={styles.input}
                                required
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                style={styles.showPasswordBtn}
                            >
                                {showPassword ? '🙈' : '👁️'}
                            </button>
                        </div>
                    </div>

                    {error && (
                        <div style={styles.errorBox}>
                            <span>⚠️</span> {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            ...styles.submitBtn,
                            opacity: loading ? 0.7 : 1
                        }}
                    >
                        {loading ? (
                            <span style={styles.loadingSpinner}>⏳</span>
                        ) : (
                            <>Entrar</>
                        )}
                    </button>
                </form>

                {/* Dica de teste */}
                <div style={styles.testHint}>
                    <p style={styles.hintText}>🧪 <strong>Teste rápido:</strong></p>
                    <code style={styles.hintCode}>
                        Login: ana.X | Senha: 123456
                    </code>
                    <p style={styles.hintNote}>(substitua X pelo ID do usuário)</p>
                </div>
            </div>

            {/* Decoração */}
            <div style={styles.floatingEmoji1}>🎤</div>
            <div style={styles.floatingEmoji2}>🔊</div>
            <div style={styles.floatingEmoji3}>💬</div>
        </div>
    );
}

// ============================================
// 📊 DASHBOARD
// ============================================
function Dashboard({ user, token, onLogout, onStartChat }) {
    const [especialistas, setEspecialistas] = useState([]);
    const [selectedEspecialista, setSelectedEspecialista] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchEspecialistas();
    }, []);

    const fetchEspecialistas = async () => {
        try {
            const response = await fetch(`${API_URL}/especialistas`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setEspecialistas(data);
                if (data.length > 0) {
                    setSelectedEspecialista(data[0]);
                }
            }
        } catch (err) {
            console.error('Erro ao buscar especialistas:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.dashboardContainer}>
            {/* Header */}
            <header style={styles.dashboardHeader}>
                <div style={styles.headerLeft}>
                    <div style={styles.logoSmall}>🗣️</div>
                    <span style={styles.logoText}>FonoFlow</span>
                </div>
                <div style={styles.headerRight}>
                    <div style={styles.userBadge}>
            <span style={styles.userAvatar}>
              {user.tipo === 'CLIENTE' ? '👤' : user.tipo === 'ESPECIALISTA' ? '👩‍⚕️' : '📋'}
            </span>
                        <div style={styles.userInfo}>
                            <span style={styles.userName}>{user.nome}</span>
                            <span style={styles.userType}>{user.tipo}</span>
                        </div>
                    </div>
                    <button onClick={onLogout} style={styles.logoutBtn}>
                        Sair
                    </button>
                </div>
            </header>

            {/* Main Content */}
            <main style={styles.dashboardMain}>
                {/* Welcome Card */}
                <div style={styles.welcomeCard}>
                    <div style={styles.welcomeContent}>
                        <h1 style={styles.welcomeTitle}>
                            Olá, {user.nome.split(' ')[0]}! 👋
                        </h1>
                        <p style={styles.welcomeText}>
                            {user.tipo === 'CLIENTE'
                                ? 'Pronto para treinar sua pronúncia hoje? Vamos praticar com trava-línguas divertidos!'
                                : 'Gerencie suas sessões e acompanhe o progresso dos pacientes.'}
                        </p>
                    </div>
                    <div style={styles.welcomeDecor}>🎯</div>
                </div>

                {/* Quick Actions */}
                {user.tipo === 'CLIENTE' && (
                    <div style={styles.actionsSection}>
                        <h2 style={styles.sectionTitle}>Iniciar Treino</h2>

                        {/* Seletor de Especialista */}
                        <div style={styles.especialistaSelector}>
                            <label style={styles.selectorLabel}>Escolha seu fonoaudiólogo:</label>
                            <div style={styles.especialistaGrid}>
                                {loading ? (
                                    <div style={styles.loadingBox}>Carregando...</div>
                                ) : especialistas.length === 0 ? (
                                    <div style={styles.emptyBox}>Nenhum especialista disponível</div>
                                ) : (
                                    especialistas.map((esp) => (
                                        <div
                                            key={esp.id}
                                            onClick={() => setSelectedEspecialista(esp)}
                                            style={{
                                                ...styles.especialistaCard,
                                                ...(selectedEspecialista?.id === esp.id ? styles.especialistaCardSelected : {})
                                            }}
                                        >
                                            <div style={styles.especialistaAvatar}>👩‍⚕️</div>
                                            <div style={styles.especialistaInfo}>
                                                <span style={styles.especialistaNome}>{esp.nome}</span>
                                                <span style={styles.especialistaEspec}>{esp.especialidade}</span>
                                                <span style={styles.especialistaCrm}>CRM: {esp.crmFono}</span>
                                            </div>
                                            {selectedEspecialista?.id === esp.id && (
                                                <div style={styles.checkmark}>✓</div>
                                            )}
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>

                        {/* Botão de Iniciar */}
                        <button
                            onClick={onStartChat}
                            disabled={!selectedEspecialista}
                            style={{
                                ...styles.startChatBtn,
                                opacity: selectedEspecialista ? 1 : 0.5
                            }}
                        >
                            <span style={styles.startChatIcon}>🎤</span>
                            Iniciar Sessão de Treino
                        </button>
                    </div>
                )}

                {/* Info Cards */}
                <div style={styles.infoGrid}>
                    <div style={styles.infoCard}>
                        <div style={styles.infoIcon}>📊</div>
                        <h3 style={styles.infoTitle}>Como funciona</h3>
                        <p style={styles.infoText}>
                            Você receberá um trava-língua personalizado. Grave seu áudio e receba feedback instantâneo da IA!
                        </p>
                    </div>
                    <div style={styles.infoCard}>
                        <div style={styles.infoIcon}>🎯</div>
                        <h3 style={styles.infoTitle}>Dificuldades</h3>
                        <p style={styles.infoText}>
                            Treine fonemas específicos: R, L, S, CH, LH, X e mais. A IA adapta o conteúdo para você!
                        </p>
                    </div>
                    <div style={styles.infoCard}>
                        <div style={styles.infoIcon}>🏆</div>
                        <h3 style={styles.infoTitle}>Progresso</h3>
                        <p style={styles.infoText}>
                            Acompanhe sua evolução a cada sessão. Celebre suas conquistas!
                        </p>
                    </div>
                </div>
            </main>

            {/* Footer */}
            <footer style={styles.dashboardFooter}>
                <p>FonoFlow © 2024 - Treino de pronúncia com IA 🧠</p>
            </footer>
        </div>
    );
}

// ============================================
// 💬 SESSÃO DE CHAT/TREINO
// ============================================
function ChatSession({ user, token, onBack }) {
    const [messages, setMessages] = useState([]);
    const [sessaoId, setSessaoId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [recording, setRecording] = useState(false);
    const [audioBlob, setAudioBlob] = useState(null);
    const [dificuldade, setDificuldade] = useState('R');
    const [sessionStarted, setSessionStarted] = useState(false);
    const [sessionFinished, setSessionFinished] = useState(false);
    const [especialistaId, setEspecialistaId] = useState(8); // Default

    const mediaRecorderRef = useRef(null);
    const audioChunksRef = useRef([]);
    const messagesEndRef = useRef(null);

    const dificuldades = [
        { value: 'R', label: '🔤 R (rato, carro)', desc: 'Som vibrante' },
        { value: 'L', label: '🔤 L (lua, bola)', desc: 'Som lateral' },
        { value: 'S', label: '🔤 S (sapo, massa)', desc: 'Som fricativo' },
        { value: 'CH', label: '🔤 CH (chuva, bicho)', desc: 'Som palatal' },
        { value: 'X', label: '🔤 X (xícara, peixe)', desc: 'Som de "sh"' },
        { value: 'LH', label: '🔤 LH (palha, filho)', desc: 'Som lateral palatal' },
    ];

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    // Iniciar sessão
    const startSession = async () => {
        setLoading(true);
        try {
            const response = await fetch(`${API_URL}/api/sessao-treino/iniciar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    clienteId: user.id,
                    especialistaId: especialistaId,
                    dificuldade: dificuldade,
                    idade: 25 // Default ou pegar do perfil
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao iniciar sessão');
            }

            const data = await response.json();
            console.log('Sessão iniciada:', data);

            // Processa as mensagens retornadas
            if (Array.isArray(data)) {
                const newMessages = data.map(msg => ({
                    id: Date.now() + Math.random(),
                    type: 'bot',
                    content: msg.mensagem,
                    palavras: msg.palavras,
                    msgType: msg.tipo,
                    timestamp: new Date()
                }));
                setMessages(newMessages);

                // Pega o sessaoId da primeira mensagem
                if (data[0]?.sessaoId) {
                    setSessaoId(data[0].sessaoId);
                }
            }

            setSessionStarted(true);

        } catch (err) {
            console.error('Erro:', err);
            setMessages([{
                id: Date.now(),
                type: 'error',
                content: `Erro: ${err.message}`,
                timestamp: new Date()
            }]);
        } finally {
            setLoading(false);
        }
    };

    // Iniciar gravação
    const startRecording = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            mediaRecorderRef.current = new MediaRecorder(stream);
            audioChunksRef.current = [];

            mediaRecorderRef.current.ondataavailable = (e) => {
                audioChunksRef.current.push(e.data);
            };

            mediaRecorderRef.current.onstop = () => {
                const blob = new Blob(audioChunksRef.current, { type: 'audio/webm' });
                setAudioBlob(blob);
                stream.getTracks().forEach(track => track.stop());
            };

            mediaRecorderRef.current.start();
            setRecording(true);

        } catch (err) {
            console.error('Erro ao acessar microfone:', err);
            alert('Não foi possível acessar o microfone. Verifique as permissões.');
        }
    };

    // Parar gravação
    const stopRecording = () => {
        if (mediaRecorderRef.current && recording) {
            mediaRecorderRef.current.stop();
            setRecording(false);
        }
    };

    // Enviar áudio
    const sendAudio = async () => {
        if (!audioBlob || !sessaoId) return;

        setLoading(true);

        // Adiciona mensagem do usuário
        setMessages(prev => [...prev, {
            id: Date.now(),
            type: 'user',
            content: '🎤 Áudio enviado',
            isAudio: true,
            audioUrl: URL.createObjectURL(audioBlob),
            timestamp: new Date()
        }]);

        try {
            const formData = new FormData();
            formData.append('audio', audioBlob, 'audio.webm');

            const response = await fetch(
                `${API_URL}/api/sessao-treino/${sessaoId}/audio?usarGemini=true`,
                {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    body: formData
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.erro || 'Erro ao processar áudio');
            }

            const data = await response.json();
            console.log('Resposta do áudio:', data);

            // Processa as mensagens de resposta
            if (Array.isArray(data)) {
                const newMessages = data.map(msg => ({
                    id: Date.now() + Math.random(),
                    type: 'bot',
                    content: msg.mensagem,
                    palavras: msg.palavras,
                    msgType: msg.tipo,
                    analise: msg.analise,
                    resumoSessao: msg.resumoSessao,
                    sessaoFinalizada: msg.sessaoFinalizada,
                    timestamp: new Date()
                }));

                setMessages(prev => [...prev, ...newMessages]);

                // Verifica se a sessão foi finalizada
                const finalMsg = data.find(m => m.sessaoFinalizada);
                if (finalMsg) {
                    setSessionFinished(true);
                }
            }

            setAudioBlob(null);

        } catch (err) {
            console.error('Erro:', err);
            setMessages(prev => [...prev, {
                id: Date.now(),
                type: 'error',
                content: `Erro: ${err.message}`,
                timestamp: new Date()
            }]);
        } finally {
            setLoading(false);
        }
    };

    // Cancelar áudio gravado
    const cancelAudio = () => {
        setAudioBlob(null);
    };

    // Nova sessão
    const resetSession = () => {
        setMessages([]);
        setSessaoId(null);
        setSessionStarted(false);
        setSessionFinished(false);
        setAudioBlob(null);
    };

    return (
        <div style={styles.chatContainer}>
            {/* Header do Chat */}
            <header style={styles.chatHeader}>
                <button onClick={onBack} style={styles.backBtn}>
                    ← Voltar
                </button>
                <div style={styles.chatHeaderTitle}>
                    <span style={styles.chatHeaderIcon}>🎤</span>
                    <span>Sessão de Treino</span>
                </div>
                <div style={styles.chatHeaderBadge}>
                    {dificuldade}
                </div>
            </header>

            {/* Área de mensagens */}
            <div style={styles.messagesArea}>
                {!sessionStarted ? (
                    /* Tela inicial de configuração */
                    <div style={styles.setupContainer}>
                        <div style={styles.setupCard}>
                            <div style={styles.setupIcon}>🎯</div>
                            <h2 style={styles.setupTitle}>Configurar Treino</h2>
                            <p style={styles.setupDesc}>
                                Escolha o fonema que deseja praticar:
                            </p>

                            <div style={styles.dificuldadeGrid}>
                                {dificuldades.map((dif) => (
                                    <button
                                        key={dif.value}
                                        onClick={() => setDificuldade(dif.value)}
                                        style={{
                                            ...styles.dificuldadeBtn,
                                            ...(dificuldade === dif.value ? styles.dificuldadeBtnActive : {})
                                        }}
                                    >
                                        <span style={styles.dificuldadeLabel}>{dif.label}</span>
                                        <span style={styles.dificuldadeDesc}>{dif.desc}</span>
                                    </button>
                                ))}
                            </div>

                            <button
                                onClick={startSession}
                                disabled={loading}
                                style={styles.startBtn}
                            >
                                {loading ? '⏳ Preparando...' : '🚀 Começar Treino'}
                            </button>
                        </div>
                    </div>
                ) : (
                    /* Mensagens do chat */
                    <div style={styles.messagesList}>
                        {messages.map((msg) => (
                            <MessageBubble key={msg.id} message={msg} />
                        ))}

                        {loading && (
                            <div style={styles.typingIndicator}>
                                <span style={styles.typingDot}>●</span>
                                <span style={{...styles.typingDot, animationDelay: '0.2s'}}>●</span>
                                <span style={{...styles.typingDot, animationDelay: '0.4s'}}>●</span>
                            </div>
                        )}

                        <div ref={messagesEndRef} />
                    </div>
                )}
            </div>

            {/* Área de input/gravação */}
            {sessionStarted && !sessionFinished && (
                <div style={styles.inputArea}>
                    {audioBlob ? (
                        /* Preview do áudio gravado */
                        <div style={styles.audioPreview}>
                            <audio
                                src={URL.createObjectURL(audioBlob)}
                                controls
                                style={styles.audioPlayer}
                            />
                            <div style={styles.audioActions}>
                                <button onClick={cancelAudio} style={styles.cancelAudioBtn}>
                                    ❌ Cancelar
                                </button>
                                <button
                                    onClick={sendAudio}
                                    disabled={loading}
                                    style={styles.sendAudioBtn}
                                >
                                    {loading ? '⏳' : '📤'} Enviar
                                </button>
                            </div>
                        </div>
                    ) : (
                        /* Botão de gravação */
                        <div style={styles.recordContainer}>
                            <button
                                onClick={recording ? stopRecording : startRecording}
                                style={{
                                    ...styles.recordBtn,
                                    ...(recording ? styles.recordBtnActive : {})
                                }}
                            >
                <span style={styles.recordIcon}>
                  {recording ? '⏹️' : '🎤'}
                </span>
                                <span style={styles.recordText}>
                  {recording ? 'Parar Gravação' : 'Gravar Áudio'}
                </span>
                            </button>

                            {recording && (
                                <div style={styles.recordingIndicator}>
                                    <span style={styles.recordingDot}>●</span>
                                    Gravando...
                                </div>
                            )}
                        </div>
                    )}
                </div>
            )}

            {/* Sessão finalizada */}
            {sessionFinished && (
                <div style={styles.inputArea}>
                    <button onClick={resetSession} style={styles.newSessionBtn}>
                        🔄 Nova Sessão
                    </button>
                    <button onClick={onBack} style={styles.backToDashboardBtn}>
                        🏠 Voltar ao Início
                    </button>
                </div>
            )}
        </div>
    );
}

// ============================================
// 💬 COMPONENTE DE MENSAGEM
// ============================================
function MessageBubble({ message }) {
    const isBot = message.type === 'bot';
    const isError = message.type === 'error';

    return (
        <div style={{
            ...styles.messageBubbleContainer,
            justifyContent: isBot ? 'flex-start' : 'flex-end'
        }}>
            {isBot && (
                <div style={styles.botAvatar}>🤖</div>
            )}

            <div style={{
                ...styles.messageBubble,
                ...(isBot ? styles.botBubble : {}),
                ...(isError ? styles.errorBubble : {}),
                ...(!isBot && !isError ? styles.userBubble : {})
            }}>
                {/* Conteúdo principal */}
                <p style={styles.messageText}>{message.content}</p>

                {/* Palavras/Trava-língua */}
                {message.palavras && message.palavras.length > 0 && (
                    <div style={styles.palavrasBox}>
                        {message.palavras.map((palavra, idx) => (
                            <div key={idx} style={styles.palavraItem}>
                                📢 {palavra}
                            </div>
                        ))}
                    </div>
                )}

                {/* Resultado da análise */}
                {message.analise && (
                    <div style={styles.analiseBox}>
                        <div style={styles.analiseHeader}>
                            <span>📊 Resultado da Análise</span>
                            <span style={styles.analiseScore}>
                {message.analise.pontuacaoGeral?.toFixed(0)}%
              </span>
                        </div>

                        {message.analise.resultados?.map((res, idx) => (
                            <div key={idx} style={styles.analiseItem}>
                                <span style={styles.analiseWord}>{res.palavraEsperada}</span>
                                <span style={{
                                    ...styles.analiseStatus,
                                    color: res.acertou ? '#22c55e' : '#ef4444'
                                }}>
                  {res.acertou ? '✓' : '✗'}
                </span>
                                <span style={styles.analiseFeedback}>{res.feedback}</span>
                            </div>
                        ))}
                    </div>
                )}

                {/* Resumo final */}
                {message.resumoSessao && (
                    <div style={styles.resumoBox}>
                        <div style={styles.resumoHeader}>🏆 Resumo da Sessão</div>
                        <div style={styles.resumoStats}>
                            <div style={styles.resumoStat}>
                <span style={styles.resumoStatValue}>
                  {message.resumoSessao.totalAcertos}/{message.resumoSessao.totalPalavras}
                </span>
                                <span style={styles.resumoStatLabel}>Acertos</span>
                            </div>
                            <div style={styles.resumoStat}>
                <span style={styles.resumoStatValue}>
                  {message.resumoSessao.pontuacaoGeral?.toFixed(0)}%
                </span>
                                <span style={styles.resumoStatLabel}>Pontuação</span>
                            </div>
                        </div>

                        {message.resumoSessao.pontosFortes?.length > 0 && (
                            <div style={styles.resumoSection}>
                                <span style={styles.resumoSectionTitle}>💪 Pontos Fortes:</span>
                                {message.resumoSessao.pontosFortes.map((p, i) => (
                                    <span key={i} style={styles.resumoPoint}>• {p}</span>
                                ))}
                            </div>
                        )}

                        {message.resumoSessao.pontosAMelhorar?.length > 0 && (
                            <div style={styles.resumoSection}>
                                <span style={styles.resumoSectionTitle}>📈 A Melhorar:</span>
                                {message.resumoSessao.pontosAMelhorar.map((p, i) => (
                                    <span key={i} style={styles.resumoPoint}>• {p}</span>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* Timestamp */}
                <span style={styles.messageTime}>
          {message.timestamp?.toLocaleTimeString('pt-BR', {
              hour: '2-digit',
              minute: '2-digit'
          })}
        </span>
            </div>
        </div>
    );
}

// ============================================
// 🎨 ESTILOS
// ============================================
const styles = {
    // App Container
    app: {
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%)',
        fontFamily: "'Segoe UI', 'Roboto', sans-serif",
        position: 'relative',
        overflow: 'hidden'
    },
    bgPattern: {
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundImage: `radial-gradient(circle at 25% 25%, rgba(99, 102, 241, 0.1) 0%, transparent 50%),
                      radial-gradient(circle at 75% 75%, rgba(236, 72, 153, 0.1) 0%, transparent 50%)`,
        pointerEvents: 'none'
    },

    // Login
    loginContainer: {
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px',
        position: 'relative'
    },
    loginCard: {
        background: 'rgba(30, 41, 59, 0.9)',
        backdropFilter: 'blur(20px)',
        borderRadius: '24px',
        padding: '48px',
        width: '100%',
        maxWidth: '420px',
        boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
        border: '1px solid rgba(255, 255, 255, 0.1)',
        position: 'relative',
        zIndex: 10
    },
    loginHeader: {
        textAlign: 'center',
        marginBottom: '32px'
    },
    logoIcon: {
        fontSize: '64px',
        marginBottom: '16px',
        filter: 'drop-shadow(0 0 20px rgba(99, 102, 241, 0.5))'
    },
    loginTitle: {
        fontSize: '32px',
        fontWeight: '700',
        color: '#f8fafc',
        margin: '0 0 8px 0',
        letterSpacing: '-0.5px'
    },
    loginSubtitle: {
        color: '#94a3b8',
        fontSize: '16px',
        margin: 0
    },
    loginForm: {
        display: 'flex',
        flexDirection: 'column',
        gap: '24px'
    },
    inputGroup: {
        display: 'flex',
        flexDirection: 'column',
        gap: '8px'
    },
    inputLabel: {
        color: '#e2e8f0',
        fontSize: '14px',
        fontWeight: '500'
    },
    inputWrapper: {
        position: 'relative',
        display: 'flex',
        alignItems: 'center'
    },
    inputIcon: {
        position: 'absolute',
        left: '16px',
        fontSize: '18px',
        opacity: 0.6
    },
    input: {
        width: '100%',
        padding: '16px 48px',
        borderRadius: '12px',
        border: '2px solid rgba(255, 255, 255, 0.1)',
        background: 'rgba(15, 23, 42, 0.6)',
        color: '#f8fafc',
        fontSize: '16px',
        outline: 'none',
        transition: 'all 0.3s ease',
        boxSizing: 'border-box'
    },
    showPasswordBtn: {
        position: 'absolute',
        right: '12px',
        background: 'none',
        border: 'none',
        cursor: 'pointer',
        fontSize: '18px',
        padding: '8px',
        opacity: 0.6
    },
    errorBox: {
        background: 'rgba(239, 68, 68, 0.2)',
        border: '1px solid rgba(239, 68, 68, 0.3)',
        borderRadius: '12px',
        padding: '12px 16px',
        color: '#fca5a5',
        fontSize: '14px',
        display: 'flex',
        alignItems: 'center',
        gap: '8px'
    },
    submitBtn: {
        padding: '16px 32px',
        borderRadius: '12px',
        border: 'none',
        background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        color: '#fff',
        fontSize: '16px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        boxShadow: '0 4px 15px rgba(99, 102, 241, 0.4)'
    },
    loadingSpinner: {
        display: 'inline-block',
        animation: 'spin 1s linear infinite'
    },
    testHint: {
        marginTop: '24px',
        padding: '16px',
        background: 'rgba(99, 102, 241, 0.1)',
        borderRadius: '12px',
        textAlign: 'center'
    },
    hintText: {
        color: '#a5b4fc',
        fontSize: '13px',
        margin: '0 0 8px 0'
    },
    hintCode: {
        display: 'block',
        background: 'rgba(15, 23, 42, 0.6)',
        padding: '8px 12px',
        borderRadius: '8px',
        color: '#e2e8f0',
        fontSize: '13px'
    },
    hintNote: {
        color: '#64748b',
        fontSize: '11px',
        margin: '8px 0 0 0'
    },
    floatingEmoji1: {
        position: 'absolute',
        top: '15%',
        left: '10%',
        fontSize: '48px',
        opacity: 0.3,
        animation: 'float 6s ease-in-out infinite'
    },
    floatingEmoji2: {
        position: 'absolute',
        top: '60%',
        right: '15%',
        fontSize: '36px',
        opacity: 0.2,
        animation: 'float 8s ease-in-out infinite'
    },
    floatingEmoji3: {
        position: 'absolute',
        bottom: '20%',
        left: '20%',
        fontSize: '42px',
        opacity: 0.25,
        animation: 'float 7s ease-in-out infinite'
    },

    // Dashboard
    dashboardContainer: {
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column'
    },
    dashboardHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '16px 32px',
        background: 'rgba(30, 41, 59, 0.8)',
        backdropFilter: 'blur(10px)',
        borderBottom: '1px solid rgba(255, 255, 255, 0.1)'
    },
    headerLeft: {
        display: 'flex',
        alignItems: 'center',
        gap: '12px'
    },
    logoSmall: {
        fontSize: '32px'
    },
    logoText: {
        fontSize: '20px',
        fontWeight: '700',
        color: '#f8fafc'
    },
    headerRight: {
        display: 'flex',
        alignItems: 'center',
        gap: '16px'
    },
    userBadge: {
        display: 'flex',
        alignItems: 'center',
        gap: '12px',
        padding: '8px 16px',
        background: 'rgba(99, 102, 241, 0.2)',
        borderRadius: '12px'
    },
    userAvatar: {
        fontSize: '24px'
    },
    userInfo: {
        display: 'flex',
        flexDirection: 'column'
    },
    userName: {
        color: '#f8fafc',
        fontSize: '14px',
        fontWeight: '600'
    },
    userType: {
        color: '#a5b4fc',
        fontSize: '11px',
        textTransform: 'uppercase'
    },
    logoutBtn: {
        padding: '8px 16px',
        borderRadius: '8px',
        border: '1px solid rgba(255, 255, 255, 0.2)',
        background: 'transparent',
        color: '#94a3b8',
        fontSize: '14px',
        cursor: 'pointer',
        transition: 'all 0.2s ease'
    },
    dashboardMain: {
        flex: 1,
        padding: '32px',
        maxWidth: '1200px',
        margin: '0 auto',
        width: '100%',
        boxSizing: 'border-box'
    },
    welcomeCard: {
        background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.3) 0%, rgba(139, 92, 246, 0.3) 100%)',
        borderRadius: '20px',
        padding: '32px',
        marginBottom: '32px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    welcomeContent: {
        flex: 1
    },
    welcomeTitle: {
        fontSize: '28px',
        fontWeight: '700',
        color: '#f8fafc',
        margin: '0 0 12px 0'
    },
    welcomeText: {
        color: '#cbd5e1',
        fontSize: '16px',
        margin: 0,
        maxWidth: '500px'
    },
    welcomeDecor: {
        fontSize: '80px',
        opacity: 0.8
    },
    actionsSection: {
        background: 'rgba(30, 41, 59, 0.6)',
        borderRadius: '20px',
        padding: '32px',
        marginBottom: '32px',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    sectionTitle: {
        fontSize: '20px',
        fontWeight: '600',
        color: '#f8fafc',
        margin: '0 0 24px 0'
    },
    especialistaSelector: {
        marginBottom: '24px'
    },
    selectorLabel: {
        display: 'block',
        color: '#94a3b8',
        fontSize: '14px',
        marginBottom: '16px'
    },
    especialistaGrid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
        gap: '16px'
    },
    especialistaCard: {
        display: 'flex',
        alignItems: 'center',
        gap: '16px',
        padding: '16px',
        background: 'rgba(15, 23, 42, 0.6)',
        borderRadius: '12px',
        border: '2px solid transparent',
        cursor: 'pointer',
        transition: 'all 0.2s ease',
        position: 'relative'
    },
    especialistaCardSelected: {
        borderColor: '#6366f1',
        background: 'rgba(99, 102, 241, 0.1)'
    },
    especialistaAvatar: {
        fontSize: '40px'
    },
    especialistaInfo: {
        display: 'flex',
        flexDirection: 'column',
        gap: '4px'
    },
    especialistaNome: {
        color: '#f8fafc',
        fontSize: '16px',
        fontWeight: '600'
    },
    especialistaEspec: {
        color: '#a5b4fc',
        fontSize: '13px'
    },
    especialistaCrm: {
        color: '#64748b',
        fontSize: '12px'
    },
    checkmark: {
        position: 'absolute',
        top: '12px',
        right: '12px',
        width: '24px',
        height: '24px',
        background: '#6366f1',
        borderRadius: '50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#fff',
        fontWeight: 'bold',
        fontSize: '14px'
    },
    loadingBox: {
        padding: '32px',
        textAlign: 'center',
        color: '#94a3b8'
    },
    emptyBox: {
        padding: '32px',
        textAlign: 'center',
        color: '#64748b'
    },
    startChatBtn: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '12px',
        width: '100%',
        padding: '20px 32px',
        borderRadius: '16px',
        border: 'none',
        background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        color: '#fff',
        fontSize: '18px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        boxShadow: '0 4px 20px rgba(99, 102, 241, 0.4)'
    },
    startChatIcon: {
        fontSize: '24px'
    },
    infoGrid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '20px'
    },
    infoCard: {
        background: 'rgba(30, 41, 59, 0.6)',
        borderRadius: '16px',
        padding: '24px',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    infoIcon: {
        fontSize: '40px',
        marginBottom: '16px'
    },
    infoTitle: {
        fontSize: '18px',
        fontWeight: '600',
        color: '#f8fafc',
        margin: '0 0 8px 0'
    },
    infoText: {
        color: '#94a3b8',
        fontSize: '14px',
        margin: 0,
        lineHeight: '1.6'
    },
    dashboardFooter: {
        padding: '16px 32px',
        textAlign: 'center',
        color: '#64748b',
        fontSize: '13px',
        borderTop: '1px solid rgba(255, 255, 255, 0.05)'
    },

    // Chat
    chatContainer: {
        height: '100vh',
        display: 'flex',
        flexDirection: 'column',
        background: 'linear-gradient(180deg, #0f172a 0%, #1e293b 100%)'
    },
    chatHeader: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '16px 24px',
        background: 'rgba(30, 41, 59, 0.9)',
        backdropFilter: 'blur(10px)',
        borderBottom: '1px solid rgba(255, 255, 255, 0.1)'
    },
    backBtn: {
        padding: '8px 16px',
        borderRadius: '8px',
        border: 'none',
        background: 'rgba(255, 255, 255, 0.1)',
        color: '#e2e8f0',
        fontSize: '14px',
        cursor: 'pointer'
    },
    chatHeaderTitle: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        color: '#f8fafc',
        fontSize: '18px',
        fontWeight: '600'
    },
    chatHeaderIcon: {
        fontSize: '24px'
    },
    chatHeaderBadge: {
        padding: '4px 12px',
        borderRadius: '20px',
        background: 'rgba(99, 102, 241, 0.3)',
        color: '#a5b4fc',
        fontSize: '14px',
        fontWeight: '600'
    },
    messagesArea: {
        flex: 1,
        overflow: 'auto',
        padding: '24px'
    },
    setupContainer: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100%'
    },
    setupCard: {
        background: 'rgba(30, 41, 59, 0.8)',
        borderRadius: '24px',
        padding: '40px',
        maxWidth: '600px',
        width: '100%',
        textAlign: 'center',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    setupIcon: {
        fontSize: '64px',
        marginBottom: '16px'
    },
    setupTitle: {
        fontSize: '28px',
        fontWeight: '700',
        color: '#f8fafc',
        margin: '0 0 12px 0'
    },
    setupDesc: {
        color: '#94a3b8',
        fontSize: '16px',
        margin: '0 0 32px 0'
    },
    dificuldadeGrid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
        gap: '12px',
        marginBottom: '32px'
    },
    dificuldadeBtn: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '4px',
        padding: '16px',
        borderRadius: '12px',
        border: '2px solid rgba(255, 255, 255, 0.1)',
        background: 'rgba(15, 23, 42, 0.6)',
        cursor: 'pointer',
        transition: 'all 0.2s ease'
    },
    dificuldadeBtnActive: {
        borderColor: '#6366f1',
        background: 'rgba(99, 102, 241, 0.2)'
    },
    dificuldadeLabel: {
        color: '#f8fafc',
        fontSize: '14px',
        fontWeight: '600'
    },
    dificuldadeDesc: {
        color: '#64748b',
        fontSize: '11px'
    },
    startBtn: {
        padding: '16px 48px',
        borderRadius: '12px',
        border: 'none',
        background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        color: '#fff',
        fontSize: '18px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.3s ease'
    },
    messagesList: {
        display: 'flex',
        flexDirection: 'column',
        gap: '16px'
    },
    typingIndicator: {
        display: 'flex',
        gap: '4px',
        padding: '16px',
        alignSelf: 'flex-start'
    },
    typingDot: {
        color: '#6366f1',
        animation: 'pulse 1.4s infinite'
    },
    messageBubbleContainer: {
        display: 'flex',
        alignItems: 'flex-end',
        gap: '8px'
    },
    botAvatar: {
        width: '32px',
        height: '32px',
        borderRadius: '50%',
        background: 'rgba(99, 102, 241, 0.3)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: '16px',
        flexShrink: 0
    },
    messageBubble: {
        maxWidth: '80%',
        padding: '16px 20px',
        borderRadius: '20px',
        position: 'relative'
    },
    botBubble: {
        background: 'rgba(30, 41, 59, 0.9)',
        border: '1px solid rgba(255, 255, 255, 0.1)',
        borderBottomLeftRadius: '4px'
    },
    userBubble: {
        background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        borderBottomRightRadius: '4px'
    },
    errorBubble: {
        background: 'rgba(239, 68, 68, 0.2)',
        border: '1px solid rgba(239, 68, 68, 0.3)'
    },
    messageText: {
        color: '#f8fafc',
        fontSize: '15px',
        margin: 0,
        lineHeight: '1.5',
        whiteSpace: 'pre-wrap'
    },
    palavrasBox: {
        marginTop: '16px',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px'
    },
    palavraItem: {
        background: 'rgba(99, 102, 241, 0.2)',
        padding: '16px 20px',
        borderRadius: '12px',
        color: '#e2e8f0',
        fontSize: '18px',
        fontWeight: '500',
        textAlign: 'center',
        border: '1px dashed rgba(99, 102, 241, 0.4)'
    },
    analiseBox: {
        marginTop: '16px',
        background: 'rgba(15, 23, 42, 0.6)',
        borderRadius: '12px',
        padding: '16px',
        border: '1px solid rgba(255, 255, 255, 0.1)'
    },
    analiseHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '12px',
        color: '#a5b4fc',
        fontSize: '14px',
        fontWeight: '600'
    },
    analiseScore: {
        background: 'rgba(99, 102, 241, 0.3)',
        padding: '4px 12px',
        borderRadius: '20px',
        color: '#f8fafc',
        fontWeight: '700'
    },
    analiseItem: {
        display: 'flex',
        alignItems: 'center',
        gap: '12px',
        padding: '8px 0',
        borderBottom: '1px solid rgba(255, 255, 255, 0.05)'
    },
    analiseWord: {
        color: '#f8fafc',
        fontWeight: '500',
        minWidth: '100px'
    },
    analiseStatus: {
        fontWeight: 'bold',
        fontSize: '18px'
    },
    analiseFeedback: {
        color: '#94a3b8',
        fontSize: '13px',
        flex: 1
    },
    resumoBox: {
        marginTop: '16px',
        background: 'linear-gradient(135deg, rgba(34, 197, 94, 0.2) 0%, rgba(16, 185, 129, 0.2) 100%)',
        borderRadius: '16px',
        padding: '20px',
        border: '1px solid rgba(34, 197, 94, 0.3)'
    },
    resumoHeader: {
        fontSize: '18px',
        fontWeight: '700',
        color: '#f8fafc',
        marginBottom: '16px',
        textAlign: 'center'
    },
    resumoStats: {
        display: 'flex',
        justifyContent: 'center',
        gap: '32px',
        marginBottom: '16px'
    },
    resumoStat: {
        textAlign: 'center'
    },
    resumoStatValue: {
        display: 'block',
        fontSize: '32px',
        fontWeight: '700',
        color: '#22c55e'
    },
    resumoStatLabel: {
        color: '#94a3b8',
        fontSize: '12px',
        textTransform: 'uppercase'
    },
    resumoSection: {
        marginTop: '12px'
    },
    resumoSectionTitle: {
        display: 'block',
        color: '#e2e8f0',
        fontSize: '14px',
        fontWeight: '600',
        marginBottom: '8px'
    },
    resumoPoint: {
        display: 'block',
        color: '#94a3b8',
        fontSize: '13px',
        marginLeft: '8px'
    },
    messageTime: {
        display: 'block',
        color: 'rgba(255, 255, 255, 0.4)',
        fontSize: '11px',
        marginTop: '8px',
        textAlign: 'right'
    },
    inputArea: {
        padding: '16px 24px',
        background: 'rgba(30, 41, 59, 0.9)',
        borderTop: '1px solid rgba(255, 255, 255, 0.1)',
        display: 'flex',
        gap: '12px',
        alignItems: 'center',
        justifyContent: 'center'
    },
    recordContainer: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '12px',
        width: '100%'
    },
    recordBtn: {
        display: 'flex',
        alignItems: 'center',
        gap: '12px',
        padding: '16px 48px',
        borderRadius: '50px',
        border: 'none',
        background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        color: '#fff',
        fontSize: '18px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        boxShadow: '0 4px 20px rgba(99, 102, 241, 0.4)'
    },
    recordBtnActive: {
        background: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)',
        boxShadow: '0 4px 20px rgba(239, 68, 68, 0.4)',
        animation: 'pulse 1s infinite'
    },
    recordIcon: {
        fontSize: '24px'
    },
    recordText: {},
    recordingIndicator: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        color: '#ef4444',
        fontSize: '14px',
        fontWeight: '500'
    },
    recordingDot: {
        animation: 'blink 1s infinite'
    },
    audioPreview: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '12px',
        width: '100%',
        maxWidth: '400px'
    },
    audioPlayer: {
        width: '100%',
        height: '40px'
    },
    audioActions: {
        display: 'flex',
        gap: '12px'
    },
    cancelAudioBtn: {
        padding: '10px 20px',
        borderRadius: '8px',
        border: '1px solid rgba(239, 68, 68, 0.5)',
        background: 'transparent',
        color: '#fca5a5',
        fontSize: '14px',
        cursor: 'pointer'
    },
    sendAudioBtn: {
        padding: '10px 24px',
        borderRadius: '8px',
        border: 'none',
        background: 'linear-gradient(135deg, #22c55e 0%, #16a34a 100%)',
        color: '#fff',
        fontSize: '14px',
        fontWeight: '600',
        cursor: 'pointer'
    },
    newSessionBtn: {
        padding: '12px 24px',
        borderRadius: '8px',
        border: 'none',
        background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        color: '#fff',
        fontSize: '14px',
        fontWeight: '600',
        cursor: 'pointer'
    },
    backToDashboardBtn: {
        padding: '12px 24px',
        borderRadius: '8px',
        border: '1px solid rgba(255, 255, 255, 0.2)',
        background: 'transparent',
        color: '#e2e8f0',
        fontSize: '14px',
        cursor: 'pointer'
    }
};

// Adicionar CSS de animações
const styleSheet = document.createElement('style');
styleSheet.textContent = `
  @keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-20px); }
  }
  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }
  @keyframes blink {
    0%, 100% { opacity: 1; }
    50% { opacity: 0; }
  }
  @keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
  input:focus {
    border-color: #6366f1 !important;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2) !important;
  }
  button:hover {
    transform: translateY(-2px);
    filter: brightness(1.1);
  }
  button:active {
    transform: translateY(0);
  }
  ::-webkit-scrollbar {
    width: 8px;
  }
  ::-webkit-scrollbar-track {
    background: rgba(15, 23, 42, 0.6);
  }
  ::-webkit-scrollbar-thumb {
    background: rgba(99, 102, 241, 0.5);
    border-radius: 4px;
  }
`;
document.head.appendChild(styleSheet);
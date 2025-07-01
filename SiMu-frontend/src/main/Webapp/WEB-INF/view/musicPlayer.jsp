<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Музыкальный плеер</title>
    <!-- Подключаем Font Awesome для иконок -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Arial', sans-serif;
            background-color: #121212;
            color: #fff;
            display: flex;
            height: 100vh;
            overflow: hidden;
        }

        /* Боковая панель */
        .sidebar {
            width: 240px;
            background-color: #000;
            padding: 20px;
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .sidebar .logo {
            color: #1ed760;
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 20px;
        }

        .sidebar a {
            color: #b3b3b3;
            text-decoration: none;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .sidebar a:hover {
            color: #fff;
        }

        .sidebar a i {
            font-size: 18px;
        }

        /* Основной контент */
        .main-content {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background-color: #181818;
        }

        .playlist-header {
            display: flex;
            align-items: center;
            gap: 20px;
            margin-bottom: 30px;
        }

        .playlist-header img {
            width: 192px;
            height: 192px;
            object-fit: cover;
            box-shadow: 0 4px 60px rgba(0, 0, 0, 0.5);
        }

        .playlist-header h1 {
            font-size: 48px;
            font-weight: bold;
            margin: 0;
        }

        .playlist-header p {
            color: #b3b3b3;
            font-size: 14px;
            margin-top: 5px;
        }

        .controls {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }

        .controls button {
            background-color: #2a2a2a;
            border: 1px solid #3e3e3e;
            padding: 8px 16px;
            color: #b3b3b3;
            border-radius: 20px;
            font-size: 12px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .controls button.active {
            background-color: #1db954;
            color: #fff;
            border: none;
        }

        .controls button:hover {
            background-color: #3e3e3e;
        }

        .track-list {
            width: 100%;
        }

        .track {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #282828;
            font-size: 14px;
            cursor: pointer;
        }

        .track:hover {
            background-color: #282828;
        }

        .track.playing {
            background-color: #282828;
            color: #1db954;
        }

        .track-info {
            flex: 1;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .track-info img {
            width: 40px;
            height: 40px;
            object-fit: cover;
        }

        .track-info div p:first-child {
            color: #fff;
        }

        .track-info div p:last-child {
            color: #b3b3b3;
            font-size: 12px;
        }

        .track-actions i {
            color: #b3b3b3;
            margin-left: 10px;
            cursor: pointer;
        }

        .track-actions i:hover {
            color: #fff;
        }

        .track-duration {
            color: #b3b3b3;
            font-size: 12px;
        }

        /* Панель воспроизведения */
        .player-bar {
            position: fixed;
            bottom: 0;
            width: 100%;
            background-color: #181818;
            padding: 10px 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-top: 1px solid #282828;
        }

        .player-bar .track-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .player-bar .track-info img {
            width: 40px;
            height: 40px;
        }

        .player-bar .track-info div p:first-child {
            font-size: 14px;
        }

        .player-bar .track-info div p:last-child {
            font-size: 12px;
            color: #b3b3b3;
        }

        .player-controls {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .player-controls button {
            background: none;
            border: none;
            color: #b3b3b3;
            font-size: 16px;
            cursor: pointer;
        }

        .player-controls button:hover {
            color: #fff;
        }

        .player-controls button.play-pause {
            font-size: 24px;
            color: #fff;
        }

        .player-controls .progress-bar {
            width: 300px;
            height: 4px;
            background-color: #404040;
            border-radius: 2px;
            position: relative;
            cursor: pointer;
        }

        .player-controls .progress-bar .progress {
            position: absolute;
            height: 100%;
            background-color: #1db954;
            border-radius: 2px;
        }

        .player-extras {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .player-extras i {
            color: #b3b3b3;
            cursor: pointer;
        }

        .player-extras i:hover {
            color: #fff;
        }
    </style>
</head>
<body>
<!-- Боковая панель -->
<div class="sidebar">
    <div class="logo">Музыка</div>
    <a href="#"><i class="fas fa-search"></i> Поиск</a>
    <a href="#"><i class="fas fa-home"></i> Главная</a>
    <a href="#"><i class="fas fa-list"></i> Плейлисты</a>
    <a href="#"><i class="fas fa-broadcast-tower"></i> Радио</a>
    <a href="#"><i class="fas fa-heart"></i> Коллекция</a>
    <div style="margin-top: auto;">
        <a href="#"><i class="fas fa-windows"></i> Музыка на Windows</a>
        <a href="#"><i class="fas fa-download"></i> Скачать</a>
    </div>
</div>

<!-- Основной контент -->
<div class="main-content">
    <div class="playlist-header">
        <img src="https://via.placeholder.com/192" alt="Обложка плейлиста">
        <div>
            <h1>Мне нравится</h1>
            <p>sinke1Tch_v</p>
        </div>
    </div>

    <div class="controls">
        <button><i class="fas fa-play"></i> Слушать</button>
        <button class="active">Всё</button>
        <button>Вчера</button>
        <button>Электроника</button>
        <button>Танцевальная</button>
        <button>Ран и хип-хоп</button>
        <button>Всё</button>
        <button>Грусное</button>
        <button>Сколько</button>
    </div>

    <div class="track-list" id="trackList">
        <!-- Треки будут добавлены динамически через JSP или JavaScript -->
        <%
            // Пример списка треков (можно заменить на данные из базы данных)
            String[][] tracks = {
                    {"cold air", "Sace, DJ Pointless", "01:31", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "https://via.placeholder.com/40"},
                    {"byebye.wav", "SXCREDMANE", "01:57", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "https://via.placeholder.com/40"},
                    {"Bloody Play", "$LOTHBOI", "01:55", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "https://via.placeholder.com/40"}
            };

            for (int i = 0; i < tracks.length; i++) {
        %>
        <div class="track" data-index="<%= i %>">
            <div class="track-info">
                <img src="<%= tracks[i][4] %>" alt="Обложка трека">
                <div>
                    <p><%= tracks[i][0] %></p>
                    <p><%= tracks[i][1] %></p>
                </div>
            </div>
            <div class="track-actions">
                <i class="fas fa-heart"></i>
            </div>
            <span class="track-duration"><%= tracks[i][2] %></span>
        </div>
        <% } %>
    </div>
</div>

<!-- Панель воспроизведения -->
<div class="player-bar" id="playerBar">
    <div class="track-info" id="currentTrackInfo">
        <img src="https://via.placeholder.com/40" alt="Обложка трека">
        <div>
            <p id="currentTrackTitle">Выберите трек</p>
            <p id="currentTrackArtist">-</p>
        </div>
        <div class="track-actions">
            <i class="fas fa-heart"></i>
            <i class="fas fa-ellipsis-h"></i>
        </div>
    </div>
    <div class="player-controls">
        <button id="prevBtn"><i class="fas fa-step-backward"></i></button>
        <button id="playPauseBtn" class="play-pause"><i class="fas fa-play"></i></button>
        <button id="nextBtn"><i class="fas fa-step-forward"></i></button>
        <div class="progress-bar" id="progressBar">
            <div class="progress" id="progress"></div>
        </div>
    </div>
    <div class="player-extras">
        <i class="fas fa-list"></i>
        <i class="fas fa-desktop"></i>
        <i class="fas fa-volume-up"></i>
    </div>
</div>

<!-- Элемент audio для воспроизведения -->
<audio id="audioPlayer"></audio>

<!-- JavaScript для управления плеером -->
<script>
    const audioPlayer = document.getElementById('audioPlayer');
    const playPauseBtn = document.getElementById('playPauseBtn');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const progressBar = document.getElementById('progressBar');
    const progress = document.getElementById('progress');
    const currentTrackInfo = document.getElementById('currentTrackInfo');
    const currentTrackTitle = document.getElementById('currentTrackTitle');
    const currentTrackArtist = document.getElementById('currentTrackArtist');
    const tracks = document.querySelectorAll('.track');
    const trackList = document.getElementById('trackList');

    // Массив треков (данные из JSP)
    const trackData = [
        <% for (int i = 0; i < tracks.length; i++) { %>
        {
            title: "<%= tracks[i][0] %>",
            artist: "<%= tracks[i][1] %>",
            src: "<%= tracks[i][3] %>",
            cover: "<%= tracks[i][4] %>",
            duration: "<%= tracks[i][2] %>"
        }<%= i < tracks.length - 1 ? "," : "" %>
        <% } %>
    ];

    let currentTrackIndex = 0;
    let isPlaying = false;

    // Функция для загрузки трека
    function loadTrack(index) {
        const track = trackData[index];
        audioPlayer.src = track.src;
        currentTrackTitle.textContent = track.title;
        currentTrackArtist.textContent = track.artist;
        currentTrackInfo.querySelector('img').src = track.cover;

        // Обновляем стиль активного трека
        tracks.forEach(t => t.classList.remove('playing'));
        tracks[index].classList.add('playing');
    }

    // Функция воспроизведения/паузы
    function togglePlayPause() {
        if (isPlaying) {
            audioPlayer.pause();
            playPauseBtn.querySelector('i').classList.remove('fa-pause');
            playPauseBtn.querySelector('i').classList.add('fa-play');
        } else {
            audioPlayer.play();
            playPauseBtn.querySelector('i').classList.remove('fa-play');
            playPauseBtn.querySelector('i').classList.add('fa-pause');
        }
        isPlaying = !isPlaying;
    }

    // Переключение на предыдущий трек
    function prevTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + trackData.length) % trackData.length;
        loadTrack(currentTrackIndex);
        if (isPlaying) audioPlayer.play();
    }

    // Переключение на следующий трек
    function nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % trackData.length;
        loadTrack(currentTrackIndex);
        if (isPlaying) audioPlayer.play();
    }

    // Обновление прогресс-бара
    audioPlayer.addEventListener('timeupdate', () => {
        const progressPercent = (audioPlayer.currentTime / audioPlayer.duration) * 100;
        progress.style.width = `${progressPercent}%`;
    });

    // Перемотка при клике на прогресс-бар
    progressBar.addEventListener('click', (e) => {
        const width = progressBar.clientWidth;
        const clickX = e.offsetX;
        const duration = audioPlayer.duration;
        audioPlayer.currentTime = (clickX / width) * duration;
    });

    // Автоматическое переключение на следующий трек
    audioPlayer.addEventListener('ended', () => {
        nextTrack();
    });

    // Обработчики событий
    playPauseBtn.addEventListener('click', togglePlayPause);
    prevBtn.addEventListener('click', prevTrack);
    nextBtn.addEventListener('click', nextTrack);

    // Клик по треку для воспроизведения
    tracks.forEach((track, index) => {
        track.addEventListener('click', () => {
            currentTrackIndex = index;
            loadTrack(currentTrackIndex);
            audioPlayer.play();
            playPauseBtn.querySelector('i').classList.remove('fa-play');
            playPauseBtn.querySelector('i').classList.add('fa-pause');
            isPlaying = true;
        });
    });

    // Загружаем первый трек при загрузке страницы
    loadTrack(currentTrackIndex);
</script>
</body>
</html>
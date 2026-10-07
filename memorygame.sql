-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Mar 07, 2026 at 05:24 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `memorygame`
--

-- --------------------------------------------------------

--
-- Table structure for table `game_history`
--

CREATE TABLE `game_history` (
  `history_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `score` int(11) DEFAULT 0,
  `time_taken` varchar(10) DEFAULT NULL,
  `difficulty` varchar(50) DEFAULT NULL,
  `xp_earned` int(11) DEFAULT 10,
  `accuracy` int(11) DEFAULT 0,
  `play_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `game_history`
--

INSERT INTO `game_history` (`history_id`, `username`, `score`, `time_taken`, `difficulty`, `xp_earned`, `accuracy`, `play_date`) VALUES
(1, 'kim', 900, '00:13', 'Super Easy (3x4)', 90, 100, '2026-02-28 00:34:00'),
(2, 'daisy', 860, '00:13', 'Super Easy (3x4)', 86, 100, '2026-02-28 00:41:38'),
(3, 'shin', 900, '00:24', 'Super Easy (3x4)', 90, 100, '2026-02-28 00:51:34'),
(4, 'jack', 860, '00:20', 'Super Easy (3x4)', 86, 100, '2026-02-28 01:04:52'),
(5, 'jame', 780, '00:23', 'Super Easy (3x4)', 78, 100, '2026-02-28 15:16:02'),
(6, 'shine', 860, '00:21', 'Super Easy (3x4)', 86, 100, '2026-02-28 21:11:07'),
(7, 'kim', 900, '00:13', 'Super Easy (3x4)', 90, 100, '2026-02-28 21:22:00'),
(8, 'shin', 900, '00:15', 'Super Easy (3x4)', 90, 100, '2026-02-28 21:29:00'),
(9, 'main', 900, '00:13', 'Super Easy (3x4)', 90, 100, '2026-02-28 21:41:08'),
(10, 'min', 900, '00:13', 'Super Easy (3x4)', 90, 100, '2026-02-28 21:53:55'),
(11, 'ray', 900, '00:12', 'Super Easy (3x4)', 90, 100, '2026-02-28 21:57:34'),
(12, 'ray', 900, '00:14', 'Super Easy (3x4)', 90, 100, '2026-02-28 22:01:11'),
(13, 'ray', 900, '00:14', 'Super Easy (3x4)', 90, 100, '2026-02-28 22:01:32'),
(14, 'ray', 900, '00:12', 'Super Easy (3x4)', 90, 100, '2026-02-28 22:11:05'),
(15, 'ray', 920, '00:47', 'Super Easy (3x4)', 92, 100, '2026-02-28 22:11:58'),
(16, 'shin', 820, '00:18', 'Super Easy (3x4)', 82, 100, '2026-02-28 22:30:43'),
(17, 'kim', 540, '00:46', 'Super Easy (3x4)', 54, 100, '2026-03-01 19:51:12'),
(18, 'kim', 860, '00:18', 'Super Easy (3x4)', 86, 100, '2026-03-01 19:54:54'),
(19, 'kim', 1000, '00:56', 'Super Easy (3x4)', 100, 100, '2026-03-01 19:56:42'),
(20, 'eaint', 860, '00:19', 'Super Easy (3x4)', 86, 100, '2026-03-01 20:51:42'),
(21, 'ray', 900, '00:12', 'Super Easy (3x4)', 90, 100, '2026-03-01 20:55:01'),
(22, 'kim', 860, '00:19', 'Super Easy (3x4)', 86, 100, '2026-03-01 20:57:33'),
(23, 'kim', 860, '00:32', 'Super Easy (3x4)', 86, 100, '2026-03-01 21:08:09'),
(24, 'kim', 900, '00:13', 'Super Easy (3x4)', 90, 100, '2026-03-01 21:09:46'),
(25, 'kim', 920, '00:36', 'Easy (4x4)', 92, 100, '2026-03-01 21:16:04'),
(26, 'kim', 820, '00:17', 'Super Easy (3x4)', 82, 100, '2026-03-01 23:27:29'),
(27, 'kai', 820, '00:19', 'Super Easy (3x4)', 82, 100, '2026-03-01 23:49:10'),
(28, 'kim', 820, '00:43', 'Super Easy (3x4)', 82, 100, '2026-03-04 14:22:04'),
(29, 'kim', 740, '00:50', 'Super Easy (3x4)', 74, 100, '2026-03-04 14:26:09');

-- --------------------------------------------------------

--
-- Table structure for table `game_reviews`
--

CREATE TABLE `game_reviews` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `rating` int(11) NOT NULL,
  `review_text` text DEFAULT NULL,
  `submission_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `game_reviews`
--

INSERT INTO `game_reviews` (`id`, `username`, `rating`, `review_text`, `submission_date`) VALUES
(1, NULL, 5, 'good game', '2026-02-28 16:39:53'),
(2, NULL, 2, '', '2026-02-28 16:40:04'),
(3, NULL, 3, '', '2026-02-28 16:40:06'),
(4, NULL, 4, '', '2026-02-28 16:40:07'),
(5, NULL, 0, 'good', '2026-02-28 16:41:45'),
(6, NULL, 5, 'nice', '2026-02-28 16:46:09'),
(7, NULL, 3, 'good game', '2026-03-01 13:38:31'),
(8, 'kim', 4, 'good', '2026-03-01 15:42:36'),
(9, 'kim', 4, 'good', '2026-03-01 15:49:07'),
(10, 'kim', 3, '', '2026-03-01 15:56:23'),
(11, 'kim', 3, 'good', '2026-03-01 15:56:49'),
(13, 'shine', 3, 'good', '2026-03-01 15:59:25'),
(14, 'kim', 1, '', '2026-03-01 16:15:31'),
(15, 'kim', 0, '', '2026-03-01 16:15:45'),
(16, 'kim', 3, 'nice good', '2026-03-01 16:21:06'),
(17, 'jin', 3, '', '2026-03-01 16:28:20'),
(18, 'kim', 3, 'nnice', '2026-03-04 03:09:43'),
(19, 'daisy', 2, 'nice', '2026-03-04 06:43:24'),
(20, 'kim', 3, 'nice', '2026-03-04 08:04:04');

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE `players` (
  `player_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `high_score` int(11) DEFAULT 0,
  `passed_level` varchar(50) DEFAULT 'None',
  `games_played` int(11) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `best_time_seconds` int(11) DEFAULT 0,
  `longest_time_seconds` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`player_id`, `username`, `password_hash`, `high_score`, `passed_level`, `games_played`, `created_at`, `best_time_seconds`, `longest_time_seconds`) VALUES
(2, 'daisy', 'daisy11', 1040, 'Super Easy (3x4)', 4, '2026-02-26 07:02:45', 9, 30),
(3, 'shin', 'shin11', 900, 'Super Easy (3x4)', 4, '2026-02-26 07:44:50', 15, 24),
(6, 'kim', 'kimkim', 1000, 'Super Easy (3x4)', 25, '2026-02-26 16:10:19', 13, 56),
(8, 'khin', 'khin11', 0, 'None', 0, '2026-02-26 16:21:33', 0, 0),
(9, 'Yin', 'yinyin', 0, 'None', 0, '2026-02-26 16:22:59', 0, 0),
(10, 'Tin', 'tintin', 860, 'Super Easy (3x4)', 3, '2026-02-26 16:41:17', 15, 61),
(11, 'tun', 'tuntun', 920, 'Super Easy (3x4)', 5, '2026-02-26 16:49:44', 19, 40),
(12, 'shoon', 'shoon11', 860, 'Super Easy (3x4)', 2, '2026-02-27 11:27:31', 23, 36),
(13, 'win', 'winwin', 860, 'Super Easy (3x4)', 6, '2026-02-27 11:38:01', 14, 32),
(14, 'wai', 'waiwai', 860, 'Super Easy (3x4)', 1, '2026-02-27 15:40:49', 13, 13),
(15, 'htet', 'htet11', 740, 'Super Easy (3x4)', 1, '2026-02-27 16:04:41', 23, 23),
(16, 'mike', 'mike11', 820, 'Super Easy (3x4)', 1, '2026-02-27 16:16:24', 23, 23),
(17, 'jin', 'jinjin', 740, 'Super Easy (3x4)', 1, '2026-02-27 17:49:46', 25, 25),
(18, 'jack', 'jack11', 860, 'Super Easy (3x4)', 1, '2026-02-27 18:34:24', 20, 20),
(19, 'jame', 'jame11', 780, 'Super Easy (3x4)', 1, '2026-02-28 08:45:32', 23, 23),
(20, 'ray', 'rayray', 920, 'Super Easy (3x4)', 6, '2026-02-28 08:49:29', 12, 47),
(21, 'shine', 'shine11', 860, 'Super Easy (3x4)', 1, '2026-02-28 10:21:52', 21, 21),
(22, 'main', 'main11', 900, 'Super Easy (3x4)', 1, '2026-02-28 15:10:46', 13, 13),
(23, 'min', 'minmin', 900, 'Super Easy (3x4)', 1, '2026-02-28 15:23:33', 13, 13),
(24, 'thin', 'thin11', 0, 'None', 0, '2026-03-01 14:02:32', 0, 0),
(25, 'eaint', 'eaint11', 860, 'Super Easy (3x4)', 1, '2026-03-01 14:21:14', 19, 19),
(26, 'kai', 'kaikai', 820, 'Super Easy (3x4)', 1, '2026-03-01 17:18:45', 19, 19);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `game_history`
--
ALTER TABLE `game_history`
  ADD PRIMARY KEY (`history_id`);

--
-- Indexes for table `game_reviews`
--
ALTER TABLE `game_reviews`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`player_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `game_history`
--
ALTER TABLE `game_history`
  MODIFY `history_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `game_reviews`
--
ALTER TABLE `game_reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `players`
--
ALTER TABLE `players`
  MODIFY `player_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

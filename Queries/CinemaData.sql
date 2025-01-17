USE [master]
GO
/****** Object:  Database [CinemaData]    Script Date: 13/1/2025 20:06:30 ******/
CREATE DATABASE [CinemaData]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'CinemaData', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER02\MSSQL\DATA\CinemaData.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'CinemaData_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER02\MSSQL\DATA\CinemaData_log.ldf' , SIZE = 73728KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [CinemaData] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [CinemaData].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [CinemaData] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [CinemaData] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [CinemaData] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [CinemaData] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [CinemaData] SET ARITHABORT OFF 
GO
ALTER DATABASE [CinemaData] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [CinemaData] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [CinemaData] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [CinemaData] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [CinemaData] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [CinemaData] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [CinemaData] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [CinemaData] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [CinemaData] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [CinemaData] SET  DISABLE_BROKER 
GO
ALTER DATABASE [CinemaData] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [CinemaData] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [CinemaData] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [CinemaData] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [CinemaData] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [CinemaData] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [CinemaData] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [CinemaData] SET RECOVERY FULL 
GO
ALTER DATABASE [CinemaData] SET  MULTI_USER 
GO
ALTER DATABASE [CinemaData] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [CinemaData] SET DB_CHAINING OFF 
GO
ALTER DATABASE [CinemaData] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [CinemaData] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [CinemaData] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [CinemaData] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'CinemaData', N'ON'
GO
ALTER DATABASE [CinemaData] SET QUERY_STORE = ON
GO
ALTER DATABASE [CinemaData] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [CinemaData]
GO
/****** Object:  User [Client]    Script Date: 13/1/2025 20:06:30 ******/
CREATE USER [Client] FOR LOGIN [Client] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  User [AdminCinema]    Script Date: 13/1/2025 20:06:30 ******/
CREATE USER [AdminCinema] FOR LOGIN [AdminCinema] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [AdminCinema]
GO
/****** Object:  Table [dbo].[Accounts]    Script Date: 13/1/2025 20:06:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Accounts](
	[account_email] [varchar](255) NOT NULL,
	[name] [varchar](100) NULL,
	[gender] [varchar](6) NULL,
	[date_of_birth] [date] NULL,
	[membership] [bit] NULL,
	[transactions] [int] NULL,
	[password_hash] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[account_email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[MovieInfo]    Script Date: 13/1/2025 20:06:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[MovieInfo](
	[Title] [nvarchar](100) NULL,
	[Director] [nvarchar](100) NULL,
	[ReleaseDate] [date] NULL,
	[Duration] [int] NULL,
	[Language] [nvarchar](100) NULL,
	[Rating] [nvarchar](10) NULL,
	[Link] [nvarchar](200) NULL,
	[id] [int] IDENTITY(1,1) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UC_Title] UNIQUE NONCLUSTERED 
(
	[Title] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Showrooms]    Script Date: 13/1/2025 20:06:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Showrooms](
	[ShowroomID] [int] IDENTITY(1,1) NOT NULL,
	[ShowroomName] [nvarchar](100) NULL,
	[MaxChairs] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[ShowroomID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Showtimes]    Script Date: 13/1/2025 20:06:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Showtimes](
	[ShowtimeID] [int] IDENTITY(1,1) NOT NULL,
	[Time] [time](7) NULL,
	[MovieId] [int] NULL,
	[Date] [date] NULL,
	[ShowroomID] [int] NULL,
	[ReservedChairs] [int] NULL,
	[Chairs_Booked] [varchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[ShowtimeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Transactions]    Script Date: 13/1/2025 20:06:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Transactions](
	[TransactionID] [int] IDENTITY(1,1) NOT NULL,
	[TransactionDate] [date] NULL,
	[MovieId] [int] NULL,
	[Amount] [decimal](18, 2) NULL,
	[SeatsPreserved] [nvarchar](max) NULL,
	[ShowroomID] [int] NULL,
	[account_email] [varchar](255) NULL,
	[ShowtimeID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[TransactionID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [dbo].[Showtimes] ADD  CONSTRAINT [DF_ReservedChairs]  DEFAULT ((0)) FOR [ReservedChairs]
GO
ALTER TABLE [dbo].[Showtimes] ADD  CONSTRAINT [DF_Chairs_Booked]  DEFAULT ('') FOR [Chairs_Booked]
GO
ALTER TABLE [dbo].[Showtimes]  WITH CHECK ADD FOREIGN KEY([MovieId])
REFERENCES [dbo].[MovieInfo] ([id])
GO
ALTER TABLE [dbo].[Showtimes]  WITH CHECK ADD FOREIGN KEY([ShowroomID])
REFERENCES [dbo].[Showrooms] ([ShowroomID])
GO
ALTER TABLE [dbo].[Transactions]  WITH CHECK ADD FOREIGN KEY([account_email])
REFERENCES [dbo].[Accounts] ([account_email])
GO
ALTER TABLE [dbo].[Transactions]  WITH CHECK ADD FOREIGN KEY([MovieId])
REFERENCES [dbo].[MovieInfo] ([id])
GO
ALTER TABLE [dbo].[Transactions]  WITH CHECK ADD FOREIGN KEY([ShowroomID])
REFERENCES [dbo].[Showrooms] ([ShowroomID])
GO
ALTER TABLE [dbo].[Transactions]  WITH CHECK ADD  CONSTRAINT [FK_Transactions_Showtimes] FOREIGN KEY([ShowtimeID])
REFERENCES [dbo].[Showtimes] ([ShowtimeID])
GO
ALTER TABLE [dbo].[Transactions] CHECK CONSTRAINT [FK_Transactions_Showtimes]
GO
ALTER TABLE [dbo].[Accounts]  WITH CHECK ADD  CONSTRAINT [CHK_Gender] CHECK  (([gender]='Female' OR [gender]='Male'))
GO
ALTER TABLE [dbo].[Accounts] CHECK CONSTRAINT [CHK_Gender]
GO
/****** Object:  StoredProcedure [dbo].[InsertShowtimes]    Script Date: 13/1/2025 20:06:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[InsertShowtimes]
AS
BEGIN
    DECLARE @CurrentDate DATE = CAST(GETDATE() AS DATE);
    DECLARE @EndDate DATE = DATEADD(DAY, 2, @CurrentDate); -- End date is two days from today

    WHILE @CurrentDate <= @EndDate
    BEGIN
        -- Insert statements for the current date
        INSERT INTO Showtimes (Time, MovieId, Date, ShowroomID)
        VALUES
            ('10:00', 4, @CurrentDate, 1),
            ('13:00', 4, @CurrentDate, 1),
            ('16:00', 3, @CurrentDate, 1),
            ('23:40', 19, @CurrentDate, 1),
            ('10:30', 4, @CurrentDate, 2),
            ('13:30', 4, @CurrentDate, 2),
            ('16:30', 26, @CurrentDate, 2),
            ('23:40', 8, @CurrentDate, 2),
            ('10:00', 25, @CurrentDate, 3),
            ('13:00', 4, @CurrentDate, 3),
            ('16:00', 3, @CurrentDate, 3),
            ('23:40', 19, @CurrentDate, 3),
            ('10:30', 12, @CurrentDate, 4),
            ('13:30', 20, @CurrentDate, 4),
            ('16:30', 26, @CurrentDate, 4),
            ('23:40', 8, @CurrentDate, 4);

        SET @CurrentDate = DATEADD(DAY, 1, @CurrentDate); -- Move to the next day
    END
END
GO
/****** Object:  StoredProcedure [dbo].[SeeAllTables]    Script Date: 13/1/2025 20:06:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[SeeAllTables]
AS
SELECT table_name
FROM information_schema.tables
WHERE table_type = 'BASE TABLE'
GO
USE [master]
GO
ALTER DATABASE [CinemaData] SET  READ_WRITE 
GO

-- Insert showtimes for the next 4 days from today.
INSERT INTO Showtimes (Time, MovieId, Date, ShowroomID)
VALUES
    -- Showroom 1
    ('10:00', 4, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 1),
    ('13:00', 4, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 1),
    ('16:00', 3, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 1),
    ('23:40', 19, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 1),
    -- Showroom 2
    ('10:30', 4, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 2),
    ('13:30', 4, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 2),
    ('16:30', 26, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 2),
    ('23:40', 8, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 2),
    -- Showroom 3
    ('10:00', 25, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 3),
    ('13:00', 4, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 3),
    ('16:00', 3, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 3),
    ('23:40', 19, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 3),
    -- Showroom 4
    ('10:30', 12, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 4),
    ('13:30', 20, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 4),
    ('16:30', 26, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 4),
    ('23:40', 8, DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 4);
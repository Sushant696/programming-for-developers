-- Create the database
CREATE DATABASE twitter_analysis;

--  Use the database
USE twitter_analysis;

--  Create the tweets table
CREATE TABLE tweets (
    user_id INT,
    tweet_id INT PRIMARY KEY,
    tweet VARCHAR(280),
    tweet_date DATE
);

--  Insert the sample data
INSERT INTO tweets (user_id, tweet_id, tweet, tweet_date) VALUES
(135, 13, 'Enjoying a great start to the day. #HappyDay #MorningVibes', '2024-02-01'),
(136, 14, 'Another #HappyDay with good vibes! #FeelGood', '2024-02-03'),
(137, 15, 'Productivity peaks! #WorkLife #ProductiveDay', '2024-02-04'),
(138, 16, 'Exploring new tech frontiers. #TechLife #Innovation', '2024-02-04'),
(139, 17, 'Gratitude for today''s moments. #HappyDay #Thankful', '2024-02-05'),
(140, 18, 'Innovation drives us. #TechLife #FutureTech', '2024-02-07'),
(141, 19, 'Connecting with nature''s serenity. #Nature #Peaceful', '2024-02-09');


--  Create a helper table for splitting tweets
CREATE TABLE numbers (n INT);
INSERT INTO numbers VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

--  Create a temporary table to store extracted hashtags
CREATE TEMPORARY TABLE extracted_hashtags (
    tweet_id INT,
    hashtag VARCHAR(50)
);

--  Insert hashtags using string manipulation
INSERT INTO extracted_hashtags
SELECT 
    t.tweet_id,
    SUBSTRING_INDEX(SUBSTRING_INDEX(t.tweet, ' ', n.n), ' ', -1) AS hashtag
FROM 
    tweets t
JOIN 
    numbers n ON n.n <= (LENGTH(t.tweet) - LENGTH(REPLACE(t.tweet, ' ', '')) + 1)
WHERE 
    t.tweet_date BETWEEN '2024-02-01' AND '2024-02-29'
    AND SUBSTRING_INDEX(SUBSTRING_INDEX(t.tweet, ' ', n.n), ' ', -1) LIKE '#%';
select * from extracted_hashtags;

-- Query to find the top 3 trending hashtags
SELECT 
    hashtag,
    COUNT(*) AS count
FROM 
    extracted_hashtags
GROUP BY 
    hashtag
ORDER BY 
    count DESC,
    hashtag DESC
LIMIT 3;

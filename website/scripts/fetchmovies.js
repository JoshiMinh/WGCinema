const fs = require('fs');
const path = require('path');
const https = require('https');

const apiKey = 'AIzaSyDCDoSiW9-_PCdxlph96TRQpW1naXde_cg'; // Replace with your YouTube Data API key
const directoryPaths = ['MoviesShowing', 'MoviesUpcoming'];
const outputFile = 'movies.txt';

// Function to fetch JPG file names in a directory
function fetchJPGFileNames(directory) {
    return fs.readdirSync(directory)
        .filter(file => path.extname(file).toLowerCase() === '.jpg');
}

// Function to fetch YouTube URL for a given movie name
function fetchYouTubeUrl(query) {
    const apiUrl = `https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=${encodeURIComponent(query)}&maxResults=1&key=${apiKey}`;
    return new Promise((resolve, reject) => {
        https.get(apiUrl, (response) => {
            let data = '';

            response.on('data', (chunk) => {
                data += chunk;
            });

            response.on('end', () => {
                try {
                    const result = JSON.parse(data);
                    const videos = result.items;
                    if (videos.length > 0) {
                        const firstVideo = videos[0];
                        const videoId = firstVideo.id.videoId;
                        const videoUrl = `https://www.youtube.com/embed/v=${videoId}`;
                        resolve(videoUrl);
                    } else {
                        resolve('No video found');
                    }
                } catch (error) {
                    reject('Error parsing JSON response from YouTube API.');
                }
            });
        }).on('error', (error) => {
            reject(`Error fetching data from YouTube API: ${error.message}`);
        });
    });
}

// Function to write file names to a text file
function writeToFile(fileNames) {
    fs.writeFileSync(outputFile, fileNames.join('\n'));
}

// Main function to fetch file names from directories, fetch YouTube URLs, and write to file
async function fetchAndWriteFileNames() {
    let allFileNames = [];

    for (const directory of directoryPaths) {
        const fileNames = fetchJPGFileNames(directory);

        for (const fileName of fileNames) {
            const movieName = path.basename(fileName, path.extname(fileName));
            const query = `${movieName} 2024 Trailer`;
            try {
                const youtubeUrl = await fetchYouTubeUrl(query);
                allFileNames.push(`${directory}\\${fileName} - ${youtubeUrl}`);
            } catch (error) {
                console.error(error);
            }
        }
    }

    writeToFile(allFileNames);
    console.log(`File names written to ${outputFile}`);
}

fetchAndWriteFileNames();
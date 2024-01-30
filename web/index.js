const apiEndpoint = "/api/players";
const results = document.getElementById("results");

async function fetchAndDisplayPlayers(filterType, filterValue) {
  results.innerHTML = "Searching for players...";

  try {
    const response = await fetch(apiEndpoint, {
      method: "GET",
      headers: {
        "X-Filter-Type": filterType,
        "X-Filter-Value": filterValue,
      },
    });
    const players = await response.json();

    displayPlayers(players);
  } catch (error) {
    console.error("Error fetching players:", error);
    results.innerHTML = "Error fetching players.";
  }
}

function searchPlayers() {
  const searchValue = document.getElementById("search").value;
  document.getElementById("gender").value = "";
  if (searchValue.length > 2) {
    fetchAndDisplayPlayers("search", searchValue);
  } else {
    results.innerHTML = "";
  }
}

function filterPlayers() {
  const genderValue = document.getElementById("gender").value;
  document.getElementById("search").value = "";
  if (genderValue) {
    fetchAndDisplayPlayers("gender", genderValue);
  } else {
    results.innerHTML = "";
  }
}

function displayPlayers(players) {
  results.innerHTML = players.length > 0 ? "" : "No players found.";
  const downloadButton = document.getElementById("download-results");
  downloadButton.classList.toggle("cursor-not-allowed", players.length === 0);
  downloadButton.disabled = players.length === 0;

  players.forEach((player) => {
    const playerElement = document.createElement("div");
    playerElement.className = "p-4 mb-2 bg-white rounded shadow cursor-pointer";
    playerElement.innerHTML = getPlayerHTML(player);

    playerElement.addEventListener("click", () => {
      window.location.href = `/player.html?playerId=${player._id}`;
    });

    results.appendChild(playerElement);
  });
}

function getPlayerHTML(player) {
  return `
    <p><strong>Name:</strong> ${player.firstname} ${player.lastname}</p>
    <p><strong>Club:</strong> ${player.club}</p>
    <p><strong>Title:</strong> ${player.title}</p>
    <p><strong>Gender:</strong> ${player.gender}</p>
    <p><strong>Status:</strong> ${player.status}</p>
    <!-- Add more player details here -->
  `;
}

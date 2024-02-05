const apiEndpoint = "/api";
const results = document.getElementById("results");

async function fetchAndDisplayPlayers(queryKey, queryValue) {
  results.innerHTML = "Searching for players...";
  try {
    const response = await fetch(`${apiEndpoint}/players`, {
      method: "GET",
      headers: { "X-Query-Key": queryKey, "X-Query-Value": queryValue },
    });
    const players = await response.json();
    displayPlayers(players);
  } catch (error) {
    console.error("Error fetching players:", error);
    results.innerHTML = "Error fetching players";
  }
}

async function displayAllPlayers() {
  results.innerHTML = "Loading players...";
  try {
    const response = await fetch(`${apiEndpoint}/players`);
    const players = await response.json();
    displayPlayers(players);
  } catch (error) {
    console.error("Error fetching players:", error);
    results.innerHTML = "Error fetching players";
  }
}

function debounce(func, delay = 300) {
  let debounceTimer;
  return function () {
    const context = this;
    const args = arguments;
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => func.apply(context, args), delay);
  };
}

const displayAllPlayersDebounced = debounce(displayAllPlayers);
const fetchAndDisplayPlayersDebounced = debounce(fetchAndDisplayPlayers);

function searchPlayers() {
  const searchValue = document.getElementById("search").value;
  document.getElementById("gender").value = "";
  if (searchValue.length) {
    fetchAndDisplayPlayersDebounced("search", searchValue);
  } else {
    displayAllPlayersDebounced();
  }
}

function clearSearch() {
  document.getElementById("search").value = "";
  displayAllPlayersDebounced();
}

function filterPlayers() {
  const genderValue = document.getElementById("gender").value;
  document.getElementById("search").value = "";
  if (genderValue) {
    fetchAndDisplayPlayersDebounced("gender", genderValue);
  } else {
    displayAllPlayersDebounced();
  }
}

function displayPlayers(players) {
  results.innerHTML = players.length > 0 ? "" : "No players found";
  const downloadButton = document.getElementById("download-results");
  downloadButton.classList.toggle("cursor-not-allowed", players.length === 0);
  downloadButton.disabled = players.length === 0;
  players.forEach((player) => {
    const playerElement = document.createElement("div");
    playerElement.className =
      "p-0 m-0 bg-transparent rounded-xl shadow cursor-pointer border border-1.5 border-[#BE995E]";
    playerElement.innerHTML = getPlayerHTML(player);
    playerElement.addEventListener("click", () => {
      window.location.href = `/web/player.html?playerId=${player._id}`;
    });
    results.appendChild(playerElement);
  });
}

function getPlayerHTML(player) {
  return `
    <div class="h-full flex flex-col justify-between">
      <div class="h-44 flex justify-center">
        <img
          src="${player.avatar}"
          alt="${player.firstname} ${player.lastname}"
          class="h-full"
        />
      </div>
      <div
        class="h-full bg-gradient-to-r from-[#8E6233] via-[#D3B589] to-[#BE995E] text-black pl-3 py-2 rounded-b-lg"
      >
        <p><strong>Name:</strong> ${player.firstname} ${player.lastname}</p>
        <p><strong>Club:</strong> ${player.club}</p>
        <p><strong>Title:</strong> ${player.title}</p>
        <p><strong>Gender:</strong> ${player.gender}</p>
        <p><strong>Status:</strong> ${player.status}</p>
      </div>
    </div>
    <!-- Add more player details here -->
  `;
}

function sortPlayers() {
  document.getElementById("gender").value = "";
  document.getElementById("search").value = "";
  const sortDirection =
    document.getElementById("sort").getAttribute("data-sort-asc") === "true"
      ? "asc"
      : "desc";
  document
    .getElementById("sort")
    .setAttribute("data-sort-asc", sortDirection === "asc" ? "false" : "true");
  fetchAndDisplayPlayersDebounced("sort", sortDirection);
}

async function aggregatePlayers() {
  const aggregateButton = document.getElementById("aggregate");
  aggregateButton.disabled = true;
  aggregateButton.innerHTML = "Aggregating...";
  try {
    const response = await fetch(`${apiEndpoint}/aggregate`);
    if (response.status === 200) {
      aggregateButton.innerHTML = "Aggregation complete";
    }
  } catch (error) {
    aggregateButton.innerHTML = "Aggregation failed";
    console.error("Error aggregating players:", error);
    results.innerHTML = "Error aggregating players";
  } finally {
    aggregateButton.disabled = false;
  }
}

async function downloadResults() {
  const downloadButton = document.getElementById("download-results");
  downloadButton.disabled = true;
  downloadButton.innerHTML = "Downloading...";
  const searchValue = document.getElementById("search").value;
  const genderValue = document.getElementById("gender").value;
  try {
    const searchParams = new URLSearchParams({
      gender: genderValue,
      search: searchValue,
    });
    const response = await fetch(`${apiEndpoint}/players/download`, {
      method: "GET",
      headers: { "X-Query-String-Parameters": searchParams.toString() },
    });
    if (response.status === 200) {
      const players = await response.json();
      const blob = new Blob([JSON.stringify(players, null, 2)], {
        type: "application/json",
      });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "players.json";
      a.click();
      downloadButton.innerHTML = "Download complete";
    }
  } catch (error) {
    console.error("Error downloading results:", error);
    downloadButton.innerHTML = "Download failed";
    results.innerHTML = "Error downloading results";
  } finally {
    downloadButton.disabled = false;
  }
}

displayAllPlayersDebounced();

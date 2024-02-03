const apiEndpoint = "/api";
const results = document.getElementById("results");

async function fetchAndDisplayPlayers(queryKey, queryValue) {
  results.innerHTML = "Searching for players...";

  try {
    const response = await fetch(`${apiEndpoint}/players`, {
      method: "GET",
      headers: {
        "X-Query-Key": queryKey,
        "X-Query-Value": queryValue,
      },
    });
    const players = await response.json();

    displayPlayers(players);
  } catch (error) {
    console.error("Error fetching players:", error);
    results.innerHTML = "Error fetching players.";
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
    results.innerHTML = "Error fetching players.";
  }
}

const debounce = (func, delay = 300) => {
  let debounceTimer;
  return function () {
    const context = this;
    const args = arguments;
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => func.apply(context, args), delay);
  };
};

const displayAllPlayersDebounced = debounce(displayAllPlayers, 300);
const fetchAndDisplayPlayersDebounced = debounce(fetchAndDisplayPlayers, 300);

function searchPlayers() {
  const searchValue = document.getElementById("search").value;
  document.getElementById("gender").value = "";

  if (searchValue.length) {
    fetchAndDisplayPlayersDebounced("search", searchValue);
  } else {
    displayAllPlayersDebounced();
  }
}

function filterPlayers() {
  const genderValue = document.getElementById("gender").value;
  document.getElementById("search").value = "";

  if (genderValue) {
    fetchAndDisplayPlayers("gender", genderValue);
  } else {
    displayAllPlayers();
  }
}

function displayPlayers(players) {
  results.innerHTML = players.length > 0 ? "" : "No players found.";
  const downloadButton = document.getElementById("download-results");
  downloadButton.classList.toggle("cursor-not-allowed", players.length === 0);
  downloadButton.disabled = players.length === 0;

  players.forEach((player) => {
    const playerElement = document.createElement("div");
    playerElement.className =
      "pt-4 mb-2 bg-transparent rounded-xl shadow cursor-pointer border border-1.5 border-[#BE995E]";
    playerElement.innerHTML = getPlayerHTML(player);

    playerElement.addEventListener("click", () => {
      window.location.href = `/web/player.html?playerId=${player._id}`;
    });

    results.appendChild(playerElement);
  });
}

function getPlayerHTML(player) {
  return `
    <div>
      <div class="h-44 flex justify-center">
        <img
          src="${player.avatar}"
          alt="${player.firstname} ${player.lastname}"
          class="h-full"
        />
      </div>
      <div
        class="bg-gradient-to-r from-[#8E6233] via-[#D3B589] to-[#BE995E] text-black pl-3 py-2 rounded-b-lg"
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
  fetchAndDisplayPlayers("sort", sortDirection);
}

async function aggregatePlayers() {
  const aggregateButton = document.getElementById("aggregate");
  aggregateButton.disabled = true;
  aggregateButton.innerHTML = "Aggregating...";
  try {
    const response = await fetch(`${apiEndpoint}/aggregate`);
    if (response.status === 200) {
      results.innerHTML = "Aggregation was successful.";
    }
  } catch (error) {
    console.error("Error aggregating players:", error);
    results.innerHTML = "Error aggregating players.";
  } finally {
    aggregateButton.disabled = false;
    aggregateButton.innerHTML = "Count Players by Gender";
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
      headers: {
        "X-Query-String-Parameters": searchParams.toString(),
      },
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
    }
  } catch (error) {
    console.error("Error downloading results:", error);
    results.innerHTML = "Error downloading results.";
  } finally {
    downloadButton.disabled = false;
    downloadButton.innerHTML = "Download Results";
  }
}

displayAllPlayers();

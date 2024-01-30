async function searchPlayers() {
  const searchValue = document.getElementById("search").value;
  document.getElementById("gender").value = "";
  if (searchValue.length > 2) {
    document.getElementById("results").innerHTML = "Searching for players...";
    const apiEndpoint = `/api/players/${searchValue}`;

    try {
      const response = await fetch(apiEndpoint, {
        method: "GET",
        headers: {
          "X-Filter": "search",
        },
      });
      const players = await response.json();
      if (players && players.length > 0) {
        displayPlayers(players);
      } else {
        document.getElementById("results").innerHTML = "No players found.";
      }
    } catch (error) {
      console.error("Error fetching players:", error);
      document.getElementById("results").innerHTML = "Error fetching players.";
    }
  } else {
    document.getElementById("results").innerHTML = "";
  }
}

async function filterPlayers() {
  const genderValue = document.getElementById("gender").value;
  document.getElementById("search").value = "";
  if (genderValue) {
    document.getElementById("results").innerHTML = "Searching for players...";
    const apiEndpoint = `/api/players/${genderValue}`;

    try {
      const response = await fetch(apiEndpoint, {
        method: "GET",
        headers: {
          "X-Filter": "gender",
        },
      });
      const players = await response.json();
      if (players && players.length > 0) {
        displayPlayers(players);
      } else {
        document.getElementById("results").innerHTML = "No players found.";
      }
    } catch (error) {
      console.error("Error fetching players:", error);
      document.getElementById("results").innerHTML = "Error fetching players.";
    }
  } else {
    document.getElementById("results").innerHTML = "";
  }
}

function displayPlayers(players) {
  const resultsDiv = document.getElementById("results");
  const downloadButton = document.getElementById("download-results");

  resultsDiv.innerHTML = ""; // Clear previous results

  // Check if there are players and update button state
  if (players.length > 0) {
    downloadButton.classList.remove("cursor-not-allowed");
    downloadButton.removeAttribute("disabled");
    downloadButton.classList.add("cursor-pointer");

    // Create and append the player elements to the resultsDiv
    players.forEach((player) => {
      const playerElement = document.createElement("div");
      playerElement.className =
        "p-4 mb-2 bg-white rounded shadow cursor-pointer";
      playerElement.innerHTML = `
        <p><strong>Name:</strong> ${player.firstname} ${player.lastname}</p>
        <p><strong>Club:</strong> ${player.club}</p>
        <p><strong>Title:</strong> ${player.title}</p>
        <p><strong>Gender:</strong> ${player.gender}</p>
        <p><strong>Status:</strong> ${player.status}</p>
        <!-- Add more player details here -->
      `;

      // Add click event for redirection
      playerElement.addEventListener("click", () => {
        // Replace 'playerPageURL' with the actual URL of the player
        window.location.href = `/player.html?playerId=${player._id}`;
      });

      resultsDiv.appendChild(playerElement);
    });
  } else {
    downloadButton.classList.add("cursor-not-allowed");
    downloadButton.setAttribute("disabled", "disabled");
    // Display a message indicating no results were found
    resultsDiv.innerHTML =
      "<p class='text-center text-dark'>No players found.</p>";
  }
}

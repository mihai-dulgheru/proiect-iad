const apiEndpoint = "/api/players";
const results = document.getElementById("results");

const DUMMY_DATA = [
  {
    firstname: "Magnus",
    lastname: "Carlsen",
    club: "World Chess Club",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Vishy",
    lastname: "Anand",
    club: "International Chess Association",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Judith",
    lastname: "Polgar",
    club: "Hungarian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Hou",
    lastname: "Yifan",
    club: "Chinese Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Levon",
    lastname: "Aronian",
    club: "Armenian Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Nona",
    lastname: "Gaprindashvili",
    club: "Georgian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Garry",
    lastname: "Kasparov",
    club: "Russian Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Judit",
    lastname: "Polgar",
    club: "Hungarian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Viswanathan",
    lastname: "Anand",
    club: "International Chess Association",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Maia",
    lastname: "Chiburdanidze",
    club: "Georgian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Bobby",
    lastname: "Fischer",
    club: "United States Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Pia",
    lastname: "Cramling",
    club: "Swedish Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Fabiano",
    lastname: "Caruana",
    club: "United States Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Nigel",
    lastname: "Short",
    club: "English Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Antoaneta",
    lastname: "Stefanova",
    club: "Bulgarian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Mikhail",
    lastname: "Tal",
    club: "Latvian Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Nana",
    lastname: "Dzagnidze",
    club: "Georgian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Alexander",
    lastname: "Alekhine",
    club: "Russian Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Kateryna",
    lastname: "Lagno",
    club: "Russian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Maxime",
    lastname: "Vachier-Lagrave",
    club: "French Chess Federation",
    title: "Grandmaster",
    gender: "Male",
    status: "Active",
    avatar: "/images/player.svg",
  },
  {
    firstname: "Judit",
    lastname: "Polgar",
    club: "Hungarian Chess Federation",
    title: "Grandmaster",
    gender: "Female",
    status: "Active",
    avatar: "/images/player.svg",
  },
];

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

    // comment this line for testing and add DUMMY_DATA in the displayPlayers function
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
  console.log(players);
  results.innerHTML = players.length > 0 ? "" : "No players found.";
  const downloadButton = document.getElementById("download-results");
  downloadButton.classList.toggle("cursor-not-allowed", players.length === 0);
  downloadButton.disabled = players.length === 0;

  players.forEach((player) => {
    const playerElement = document.createElement("div");
    playerElement.className =
      "pt-4 mb-2 bg-transparent rounded-xl shadow cursor-pointer border border-1.5 border-[#BE995E] ";
    playerElement.innerHTML = getPlayerHTML(player);

    playerElement.addEventListener("click", () => {
      window.location.href = `/player.html?playerId=${player._id}`;
    });

    results.appendChild(playerElement);
  });
}

function getPlayerHTML(player) {
  return `
    <div>
      <div class='h-44 flex justify-center'>
        <img src="${player.avatar}" alt="" class='h-full'>
      </div>
      <div class="bg-gradient-to-r from-[#8E6233] via-[#D3B589] to-[#BE995E] text-black pl-3 py-2">
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

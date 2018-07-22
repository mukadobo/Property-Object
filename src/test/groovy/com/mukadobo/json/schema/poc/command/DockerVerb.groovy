package com.mukadobo.json.schema.poc.command

enum DockerVerb
{
	// list taken from https://docs.docker.com/engine/reference/commandline/docker/#child-commands
	
	ATTACH    (false),  // Attach local standard input, output, and error streams to a running container
	BUILD     (true ),  // Build an image from a Dockerfile
	CHECKPOINT(true ),  // Manage checkpoints
	COMMIT    (true ),  // Create a new image from a container’s changes
	CONFIG    (true ),  // Manage Docker configs
	CONTAINER (true ),  // Manage containers
	CP        (true ),  // Copy files/folders between a container and the local filesystem
	CREATE    (true ),  // Create a new container
	DEPLOY    (true ),  // Deploy a new stack or update an existing stack
	DIFF      (true ),  // Inspect changes to files or directories on a container’s filesystem
	EVENTS    (true ),  // Get real time events from the server
	EXEC      (true ),  // Run a command in a running container
	EXPORT    (true ),  // Export a container’s filesystem as a tar archive
	HISTORY   (true ),  // Show the history of an image
	IMAGE     (true ),  // Manage images
	IMAGES    (true ),  // List images
	IMPORT    (true ),  // Import the contents from a tarball to create a filesystem image
	INFO      (true ),  // Display system-wide information
	INSPECT   (true ),  // Return low-level information on Docker objects
	KILL      (true ),  // Kill one or more running containers
	LOAD      (true ),  // Load an image from a tar archive or STDIN
	LOGIN     (true ),  // Log in to a Docker registry
	LOGOUT    (true ),  // Log out from a Docker registry
	LOGS      (true ),  // Fetch the logs of a container
	MANIFEST  (true ),  // Manage Docker image manifests and manifest lists
	NETWORK   (true ),  // Manage networks
	NODE      (true ),  // Manage Swarm nodes
	PAUSE     (true ),  // Pause all processes within one or more containers
	PLUGIN    (true ),  // Manage plugins
	PORT      (true ),  // List port mappings or a specific mapping for the container
	PS        (true ),  // List containers
	PULL      (true ),  // Pull an image or a repository from a registry
	PUSH      (true ),  // Push an image or a repository to a registry
	RENAME    (true ),  // Rename a container
	RESTART   (true ),  // Restart one or more containers
	RM        (true ),  // Remove one or more containers
	RMI       (true ),  // Remove one or more images
	RUN       (true ),  // Run a command in a new container
	SAVE      (true ),  // Save one or more images to a tar archive (streamed to STDOUT by default)
	SEARCH    (true ),  // Search the Docker Hub for images
	SECRET    (true ),  // Manage Docker secrets
	SERVICE   (true ),  // Manage services
	STACK     (true ),  // Manage Docker stacks
	START     (true ),  // Start one or more stopped containers
	STATS     (true ),  // Display a live stream of container(s) resource usage statistics
	STOP      (true ),  // Stop one or more running containers
	SWARM     (true ),  // Manage Swarm
	SYSTEM    (true ),  // Manage Docker
	TAG       (true ),  // Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
	TOP       (true ),  // Display the running processes of a container
	TRUST     (true ),  // Manage trust on Docker images
	UNPAUSE   (true ),  // Unpause all processes within one or more containers
	UPDATE    (true ),  // Update configuration of one or more containers
	VERSION   (true ),  // Show the Docker version information
	VOLUME    (true ),  // Manage volumes
	WAIT      (true ),  // Block until one or more containers stop, then print their exit codes

	$NYI      (true ),  // A "never-to-be-implemented" verb for error handling tests
	$NOSUP    (false),  // A "never-to-be-supported"   verb for error handling tests
	
	final Boolean supported
	
	DockerVerb(Boolean supported)
	{
		this.supported = supported
	}
	
	static DockerVerb from(String text)
	{
		DockerVerb.valueOf(text?.trim()?.toUpperCase())
	}
}
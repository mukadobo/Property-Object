package com.mukadobo.json.schema.poc.command

enum DockerAction
{
	// list taken from https://docs.docker.com/engine/reference/commandline/docker/#child-commands
	
	ATTACH    ,  // Attach local standard input, output, and error streams to a running container
	BUILD     ,  // Build an image from a Dockerfile
	CHECKPOINT,  // Manage checkpoints
	COMMIT    ,  // Create a new image from a container’s changes
	CONFIG    ,  // Manage Docker configs
	CONTAINER ,  // Manage containers
	CP        ,  // Copy files/folders between a container and the local filesystem
	CREATE    ,  // Create a new container
	DEPLOY    ,  // Deploy a new stack or update an existing stack
	DIFF      ,  // Inspect changes to files or directories on a container’s filesystem
	EVENTS    ,  // Get real time events from the server
	EXEC      ,  // Run a command in a running container
	EXPORT    ,  // Export a container’s filesystem as a tar archive
	HISTORY   ,  // Show the history of an image
	IMAGE     ,  // Manage images
	IMAGES    ,  // List images
	IMPORT    ,  // Import the contents from a tarball to create a filesystem image
	INFO      ,  // Display system-wide information
	INSPECT   ,  // Return low-level information on Docker objects
	KILL      ,  // Kill one or more running containers
	LOAD      ,  // Load an image from a tar archive or STDIN
	LOGIN     ,  // Log in to a Docker registry
	LOGOUT    ,  // Log out from a Docker registry
	LOGS      ,  // Fetch the logs of a container
	MANIFEST  ,  // Manage Docker image manifests and manifest lists
	NETWORK   ,  // Manage networks
	NODE      ,  // Manage Swarm nodes
	PAUSE     ,  // Pause all processes within one or more containers
	PLUGIN    ,  // Manage plugins
	PORT      ,  // List port mappings or a specific mapping for the container
	PS        ,  // List containers
	PULL      ,  // Pull an image or a repository from a registry
	PUSH      ,  // Push an image or a repository to a registry
	RENAME    ,  // Rename a container
	RESTART   ,  // Restart one or more containers
	RM        ,  // Remove one or more containers
	RMI       ,  // Remove one or more images
	RUN       ,  // Run a command in a new container
	SAVE      ,  // Save one or more images to a tar archive (streamed to STDOUT by default)
	SEARCH    ,  // Search the Docker Hub for images
	SECRET    ,  // Manage Docker secrets
	SERVICE   ,  // Manage services
	STACK     ,  // Manage Docker stacks
	START     ,  // Start one or more stopped containers
	STATS     ,  // Display a live stream of container(s) resource usage statistics
	STOP      ,  // Stop one or more running containers
	SWARM     ,  // Manage Swarm
	SYSTEM    ,  // Manage Docker
	TAG       ,  // Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
	TOP       ,  // Display the running processes of a container
	TRUST     ,  // Manage trust on Docker images
	UNPAUSE   ,  // Unpause all processes within one or more containers
	UPDATE    ,  // Update configuration of one or more containers
	VERSION   ,  // Show the Docker version information
	VOLUME    ,  // Manage volumes
	WAIT      ,  // Block until one or more containers stop, then print their exit codes
	
	static DockerAction from(String text)
	{
		DockerAction.valueOf(text?.trim()?.toUpperCase())
	}
}
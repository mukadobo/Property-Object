package com.mukadobo.json.schema.poc.command.docker

/**
 * An {@code enum} that represents all the docker "child" commands, each with a flag indicating
 * if that command is supported. As of 2018-07-21, all "experimental" features are NOT supported.
 * Also, all Docker Swarm related features, plus a few that are only sensible from an interactive
 * terminal, are marked NOT supported.
 * <BR><BR>
 * Note that merely being marked here as supported doesn't meant that the required {@link DockerActor}
 * sub-class is available. It is expected that any DockerActor factory will fail gracefully when
 * it can't find an appropriate sub-class for a given DockerVerb. (Gracefully, as in helpful
 * error feedback, vs torturous stack traces).
 */
enum DockerVerb
{
	// list taken from https://docs.docker.com/engine/reference/commandline/docker/#child-commands
	
	ATTACH    (false),  // Attach local standard input, output, and error streams to a running container
	BUILD     (true ),  // Build an image from a Dockerfile
	CHECKPOINT(false),  // Manage checkpoints
	COMMIT    (true ),  // Create a new image from a container’s changes
	CONFIG    (false),  // Manage Docker configs
	CONTAINER (false),  // Manage containers
	CP        (true ),  // Copy files/folders between a container and the local filesystem
	CREATE    (true ),  // Create a new container
	DEPLOY    (false),  // Deploy a new stack or update an existing stack
	DIFF      (true ),  // Inspect changes to files or directories on a container’s filesystem
	EVENTS    (true ),  // Get real time events from the server
	EXEC      (true ),  // Run a command in a running container
	EXPORT    (true ),  // Export a container’s filesystem as a tar archive
	HISTORY   (true ),  // Show the history of an image
	IMAGE     (false),  // Manage images
	IMAGES    (true ),  // List images
	IMPORT    (true ),  // Import the contents from a tarball to create a filesystem image
	INFO      (true ),  // Display system-wide information
	INSPECT   (true ),  // Return low-level information on Docker objects
	KILL      (true ),  // Kill one or more running containers
	LOAD      (true ),  // Load an image from a tar archive or STDIN
	LOGIN     (true ),  // Log in to a Docker registry
	LOGOUT    (true ),  // Log out from a Docker registry
	LOGS      (true ),  // Fetch the logs of a container
	MANIFEST  (false),  // Manage Docker image manifests and manifest lists
	NETWORK   (true ),  // Manage networks
	NODE      (false),  // Manage Swarm nodes
	PAUSE     (true ),  // Pause all processes within one or more containers
	PLUGIN    (false),  // Manage plugins
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
	SEARCH    (false),  // Search the Docker Hub for images
	SECRET    (false),  // Manage Docker secrets
	SERVICE   (false),  // Manage services
	STACK     (false),  // Manage Docker stacks
	START     (true ),  // Start one or more stopped containers
	STATS     (true ),  // Display a live stream of container(s) resource usage statistics
	STOP      (true ),  // Stop one or more running containers
	SWARM     (false),  // Manage Swarm
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
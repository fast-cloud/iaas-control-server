package iaas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "object_bucket")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bucket {
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "bucket_id", length = 36)
	private String bucketId;

	@Column(name = "bucket_name", nullable = false, length = 255)
	private String bucketName;

	@Column(name = "owner_user_id", nullable = false, length = 36)
	private String ownerUserId;

	@Column(name = "status", length = 50)
	@Builder.Default
	private String status = "PENDING";

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		if (status == null) {
			status = "PENDING";
		}
	}
}

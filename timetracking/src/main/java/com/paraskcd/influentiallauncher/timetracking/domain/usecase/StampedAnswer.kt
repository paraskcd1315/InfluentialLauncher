// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.usecase

import java.time.Instant

internal data class StampedAnswer(val value: Any?, val readAt: Instant)

// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

import java.io.IOException

class TrackerRefusal(val retryAfterSeconds: Long?, detail: String) : IOException(detail)
